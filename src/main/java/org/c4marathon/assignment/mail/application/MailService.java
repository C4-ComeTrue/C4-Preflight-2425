package org.c4marathon.assignment.mail.application;

import java.util.ArrayList;
import java.util.List;

import org.c4marathon.assignment.account.domain.AccountRepository;
import org.c4marathon.assignment.mail.domain.Mail;
import org.c4marathon.assignment.mail.domain.MailRepository;
import org.c4marathon.assignment.mail.dto.CreateMailRequestDto;
import org.c4marathon.assignment.mail.dto.CreateMailResponseDto;
import org.c4marathon.assignment.mail.dto.MailInfoToSendDto;
import org.c4marathon.assignment.user.domain.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailService {
	private static final int MAX_SEND_SIZE = 1000;
	private static final int MAX_RETRY_COUNT = 3;

	private final MailSender mailSender;
	private final MailRepository mailRepository;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public MailService(MailSender mailSender, MailRepository mailRepository, AccountRepository accountRepository,
		UserRepository userRepository) {
		this.mailSender = mailSender;
		this.mailRepository = mailRepository;
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public CreateMailResponseDto createMail(CreateMailRequestDto requestDto) {
		accountRepository.findById(requestDto.accountId()).orElseThrow(() -> new RuntimeException("해당 계좌를 찾을 수 없습니다."));
		userRepository.findById(requestDto.userId()).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

		Mail mail = requestDto.toMail();
		Mail savedMail = mailRepository.saveMail(mail);

		return CreateMailResponseDto.from(savedMail);
	}

	@Scheduled(cron = "0 * * * * *")
	public void sendMail() {
		log.debug("배치 작업 활성화");

		List<MailInfoToSendDto> mailsToSend = mailRepository.findAllToSend(MAX_SEND_SIZE);

		while (!mailsToSend.isEmpty()) {
			List<Long> sentMailIds = new ArrayList<>();

			mailsToSend.forEach(info -> send(info, sentMailIds));

			mailRepository.updateSendTime(sentMailIds);

			mailsToSend = mailRepository.findAllToSend(MAX_SEND_SIZE);
		}

		log.debug("배치 작업 정상 종료");
	}

	private void send(MailInfoToSendDto info, List<Long> sentMailIds) {
		int retryCount = 0;
		boolean isSent = false;

		while (!isSent && retryCount < MAX_RETRY_COUNT) {
			try {
				mailSender.sendAccountCreateMsg(info);
				sentMailIds.add(info.getMailId());
				isSent = true;
			} catch (RuntimeException e) {
				log.warn("메일 배치 발송 중 예외 발생", e);
				log.warn("해당 메일 id: {}", info.getMailId());
				log.warn("현재까지 재시도 횟수: {}", retryCount++);
			}
		}

		if (retryCount == MAX_RETRY_COUNT) {
			log.error("id가 {}인 메일의 전송을 실패했습니다.", info.getMailId());
		}
	}

}

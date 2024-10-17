package org.c4marathon.assignment.mail.application;

import org.c4marathon.assignment.account.domain.AccountRepository;
import org.c4marathon.assignment.mail.domain.MailRepository;
import org.c4marathon.assignment.mail.dto.CreateMailDto;
import org.c4marathon.assignment.user.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	private final MailRepository mailRepository;
	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public MailService(MailRepository mailRepository, AccountRepository accountRepository,
		UserRepository userRepository) {
		this.mailRepository = mailRepository;
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	public boolean createMail(CreateMailDto createMailDto) {
		accountRepository.findById(createMailDto.accountId()).orElseThrow(() -> new RuntimeException("해당 계좌를 찾을 수 없습니다."));
		userRepository.findById(createMailDto.userId()).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

		return true;
	}
}

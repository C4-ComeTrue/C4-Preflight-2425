package org.c4marathon.assignment.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.mail.domain.MailArchive;
import org.c4marathon.assignment.mail.domain.MailStatus;
import org.c4marathon.assignment.mail.domain.repository.MailRepository;
import org.c4marathon.assignment.mail.dto.MailRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private static final String SUBJECT = "계좌 상품 설명서";

    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;

    /*
     * 메일 전송이 실패했을 경우는 어떻게 해야하지?
     * 그냥 분단위로 보내는 메일 전송을 PENDING, FAIL 를 같이 조회해서 보내야하나?
     * 근데 메일 전송 실패의 이유가 메일 서버가 문제거나 해당 이메일이 문제라는데
     * 해당 이메일이 문제라면 계속 전송 실패가 될 것 같은데 어떻게 하면 좋을지 생각해봐야함
     * create_time update_time
     * */
    @Scheduled(cron = "0 * * * * ?")
    public void sendMail() {
        log.info("메일 전송 실행");
        List<MailArchive> pendingMail = mailRepository.findMailArchiveByMailStatus(MailStatus.PENDING);

        for (MailArchive mailArchive : pendingMail) {
            try {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                mimeMessageHelper.setTo(mailArchive.getEmail());
                mimeMessageHelper.setSubject(SUBJECT);
                mimeMessageHelper.setText(mailArchive.getContent());
                javaMailSender.send(mimeMessage);

                mailArchive.changeStatusSent();
                mailArchive.setSentTime();

                mailRepository.save(mailArchive);
            } catch (MessagingException e) {
                mailArchive.changeStatusFail();
                mailRepository.save(mailArchive);
            }
        }
    }

    public void saveMailRequest(MailRequest request) {
        MailArchive mailArchive = MailArchive.of(
                request.accountId(),
                request.email(),
                request.content(),
                LocalDateTime.now()
        );

        mailRepository.save(mailArchive);
    }
}

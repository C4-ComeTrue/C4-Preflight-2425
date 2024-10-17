package org.c4marathon.assignment.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.mail.domain.MailArchive;
import org.c4marathon.assignment.mail.domain.repository.MailRepository;
import org.c4marathon.assignment.mail.dto.MailRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.c4marathon.assignment.mail.domain.MailStatus.FAIL;
import static org.c4marathon.assignment.mail.domain.MailStatus.PENDING;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private static final String SUBJECT = "계좌 상품 설명서";

    private final JavaMailSender javaMailSender;
    private final MailRepository mailRepository;

    @Scheduled(cron = "0 * * * * ?")
    public void sendMail() {
        log.info("메일 전송 실행");
        List<MailArchive> pendingMail = mailRepository.findMailArchiveByMailStatus(PENDING);
        sendMailForStatus(pendingMail);
    }

    public void resendMail() {
        log.info("전송 실패 메일 재전송");
        List<MailArchive> failMail = mailRepository.findMailArchiveByMailStatus(FAIL);
        sendMailForStatus(failMail);
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

    private void sendMailForStatus(List<MailArchive> pendingMail) {
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
                log.error("메일 전송 실패");
            }
        }
    }
}

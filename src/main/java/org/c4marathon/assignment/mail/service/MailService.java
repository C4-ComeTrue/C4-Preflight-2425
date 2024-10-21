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
        log.debug("메일 전송 실행");
        while (true) {
            List<MailArchive> pendingMail = mailRepository.findMailArchiveByStatusIn(List.of(PENDING, FAIL));
            List<MailArchive> filteringMail = pendingMail.stream()
                    .filter(m -> m.getRetryCount() <= 3)
                    .toList();

            if (filteringMail.isEmpty()) {
                break;
            }
            for (MailArchive mailArchive : filteringMail) {

                try {
                    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                    mimeMessageHelper.setTo(mailArchive.getEmail());
                    mimeMessageHelper.setSubject(SUBJECT);
                    mimeMessageHelper.setText(mailArchive.getContent());
                    javaMailSender.send(mimeMessage);

                    mailArchive.changeStatusSent();
                    mailRepository.save(mailArchive);

                } catch (MessagingException e) {
                    mailArchive.changeStatusFail();
                    mailArchive.plusRetryCount();
                    mailRepository.save(mailArchive);
                    log.debug("메일 전송 실패", e);
                }
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

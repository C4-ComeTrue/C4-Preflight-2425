package org.c4marathon.assignment.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.controller.request.MailSchedulerRequest;
import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.c4marathon.assignment.repository.MailLogRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class MailSchedulerService {
    private final MailLogRepository mailLogRepository;
    private final JavaMailSender javaMailSender;
    private static final String MAIL_SUBJECT = "계좌 생성 되었습니다";

    public void mailLogging(MailSchedulerRequest request) {
        mailLogRepository.save(new MailLog(request.userId(), request.email(), MailStatus.PENDING, request.content(), Instant.now(),Instant.now(), 0));
    }

    @Scheduled(cron = "0 * * * * *")
    protected void mailSendScheduler() {
        List<MailLog> mailLogs = mailLogRepository.findMailLogsByStatusIn(List.of(MailStatus.PENDING, MailStatus.FAIL));

        for (MailLog mailLog : mailLogs) {
            handleMailSending(mailLog);
        }
    }

    protected void handleMailSending(MailLog mailLog){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MailLog changedMailLog;

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(mailLog.getEmail());
            mimeMessageHelper.setSubject(MAIL_SUBJECT);
            mimeMessageHelper.setText(mailLog.getContent(), false);
            javaMailSender.send(mimeMessage);
            changedMailLog = new MailLog(mailLog.getId(), mailLog.getUserId(), mailLog.getEmail(), MailStatus.SUCCESS, mailLog.getContent(), mailLog.getCreateTime(), Instant.now(), mailLog.getCountRT());

        } catch (Exception e) {
            MailStatus status = MailStatus.FAIL;
            if(mailLog.getCountRT() >= 3) status = MailStatus.PERMANENT_FAIL;

            changedMailLog = new MailLog(mailLog.getId(), mailLog.getUserId(), mailLog.getEmail(), status, mailLog.getContent(), mailLog.getCreateTime(), Instant.now(), mailLog.getCountRT()+1);
            log.error("Mail send failed,  mailLogId: {}. Error: {}", mailLog.getId(), e.getMessage(), e);
        }

        mailLogRepository.save(changedMailLog);
    }
}

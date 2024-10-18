package org.c4marathon.assignment.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.config.AsyncConfig;
import org.c4marathon.assignment.controller.request.MailSchedulerRequest;
import org.c4marathon.assignment.domain.model.MailForm;
import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.c4marathon.assignment.repository.MailLogRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
public class MailSchedulerService {
    private final MailLogRepository mailLogRepository;
    private final JavaMailSender javaMailSender;
    private final AsyncConfig asyncConfig;
    private static final String MAIL_SUBJECT = "계좌생성되었습니다";

    public void mailLogging(MailSchedulerRequest request) {
        mailLogRepository.save(new MailLog(request.userId(), request.email(), MailStatus.valueOf("PENDING"), request.content(), Instant.now(),Instant.now(), 0));
    }

    @Async("customAsyncExecutor")
    protected void handleMailSending(MailForm form){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MailLog mailLog = mailLogRepository.findById(form.mailLogId()).orElseThrow(() -> new EntityNotFoundException("MailLog not found"));

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(form.to());
            mimeMessageHelper.setSubject(form.subject());
            mimeMessageHelper.setText(form.content(), false);
            javaMailSender.send(mimeMessage);

            mailLog.sendSuccess(MailStatus.SUCCESS, Instant.now());

        } catch (Exception e) {
            mailLog.sendFail(MailStatus.FAIL, Instant.now(), mailLog.getCountRT()+1);
        }

        mailLogRepository.save(mailLog);
    }

    @Scheduled(cron = "0 * * * * *")
    protected void mailSendScheduler() {
        List<MailLog> mailLogs = mailLogRepository.findMailLogsByStatusIn(Arrays.asList(MailStatus.PENDING, MailStatus.FAIL));

        for (MailLog mailLog : mailLogs) {
            handleMailSending(new MailForm(mailLog.getId(), mailLog.getEmail(), MAIL_SUBJECT, mailLog.getContent()));
        }
    }
}

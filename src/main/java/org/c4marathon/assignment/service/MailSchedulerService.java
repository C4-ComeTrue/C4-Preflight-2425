package org.c4marathon.assignment.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.controller.request.MailSchedulerRequest;
import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.c4marathon.assignment.model.MailForm;
import org.c4marathon.assignment.repository.MailLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@Component
@RequiredArgsConstructor
@EnableAsync
public class MailSchedulerService {
    private final MailLogRepository mailLogRepository;
    private final JavaMailSender javaMailSender;

    public void mailLogging(MailSchedulerRequest request) {

        mailLogRepository.save(new MailLog(request.userId(), request.email(), MailStatus.valueOf("PENDING"), request.content(), Instant.now(),Instant.now()));

        //batch로 묶어서 스케쥴링 되도록 로직 구현 예정
        //batch안에서 메일이 전송되도록 구현 예정
        handleEmailSending(new MailForm(request.email(), "계좌생성되었습니다", request.content()));
    }

    @EventListener
    @Async
    public void handleEmailSending(MailForm mail){

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(mail.to()); // 메일 수신자
            mimeMessageHelper.setSubject(mail.subject()); // 메일 제목
            mimeMessageHelper.setText(mail.content(), false); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            log.error("MailSendException happend : ",  e);
        }

    }

}

package org.c4marathon.assignment.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.entity.EmailBox;
import org.c4marathon.assignment.entity.EmailStatus;
import org.c4marathon.assignment.repository.EmailBoxRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailBoxRepository emailBoxRepository;
    private final JavaMailSender javaMailSender;
    private final Executor asyncTaskExecutor;

    public void postEmailBox(PostEmailBoxReq postEmailBoxReq) {
        emailBoxRepository.postEmailBox(postEmailBoxReq);
    }

    @Scheduled(cron = "0 * * * * *")
    public void postEmail() {
        log.debug("{} post email start", Thread.currentThread().getName());

        List<EmailBox> emailBoxes = emailBoxRepository.getEmailBoxesByStatus(EmailStatus.PENDING);
        if (emailBoxes.isEmpty()) {
            return;
        }

        List<CompletableFuture<Long>> futures = emailBoxes.stream()
                .map(emailBox -> CompletableFuture.supplyAsync(() -> makeEmail(emailBox), asyncTaskExecutor))
                .toList();

        List<Long> sentEmailIds = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .parallel()
                .toList();

        log.debug("post email end : {}개", sentEmailIds.size());

        if (sentEmailIds.isEmpty()) {
            return;
        }

        emailBoxRepository.updateStatusBySize(EmailStatus.SUCCESS, sentEmailIds);
    }

    public Long makeEmail(EmailBox emailBox) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
            mimeMessageHelper.setTo(emailBox.getEmail());
            mimeMessageHelper.setSubject("[계좌 상품 설명서]");
            mimeMessageHelper.setText(emailBox.getContent());

            javaMailSender.send(mimeMessage);
            return emailBox.getId();
        } catch (Exception e) {
            log.error("[메일 전송 실패]: [{}], {}", emailBox.getId(), e.getMessage());
            return null;
        }
    }
}

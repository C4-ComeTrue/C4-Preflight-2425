package org.c4marathon.assignment.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.entity.EmailBox;
import org.c4marathon.assignment.repository.EmailBoxRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailBoxRepository emailBoxRepository;
    private final JavaMailSender javaMailSender;

    public void postEmailBox(PostEmailBoxReq postEmailBoxReq) {
        emailBoxRepository.postEmailBox(postEmailBoxReq);
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
        }
        return null;
    }
}

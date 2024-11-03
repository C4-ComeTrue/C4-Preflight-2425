package org.c4marathon.assignment.domain.email_record.event;

import static org.c4marathon.assignment.common.configuration.AsyncConfig.*;

import org.c4marathon.assignment.domain.email_record.repository.EmailRecordReader;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailRecordEventListener {
	private final EmailRecordReader emailRecordReader;
	private final JavaMailSender javaMailSender;

	@Transactional
	@EventListener
	@Async(value = ASYNC_THREAD_POOL_NAME)
	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 1.5)
	)
	public void sendEmail(EmailRecordEvent emailRecordEvent) {
		var emailRecord = emailRecordReader.find(emailRecordEvent.getEmailRecordId());

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(emailRecordEvent.getEmail());
			helper.setSubject(emailRecord.getSubject());
			helper.setText(emailRecord.getContent(), true);

			javaMailSender.send(message);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.error("이메일 전송에 실패했습니다.");
			emailRecord.setStatusToFailed();
		}

		emailRecord.setStatusToCompleted();
	}
}

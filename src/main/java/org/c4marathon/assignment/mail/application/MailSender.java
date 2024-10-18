package org.c4marathon.assignment.mail.application;

import org.c4marathon.assignment.mail.dto.MailInfoToSendDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class MailSender {
	private final JavaMailSender javaMailSender;

	@Value("${mail.email}")
	private String email;

	public MailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendAccountCreateMsg(MailInfoToSendDto mailInfoDto) {
		try {
			MimeMessage message = getMessage(mailInfoDto);
			message.setSubject("계좌 생성이 완료되었습니다!");
			message.setText("""
				%s님, 계좌 생성이 완료되었습니다!
				생성 계좌: %s
				""".formatted(mailInfoDto.getUserNickname(), mailInfoDto.getAccountNumber())
			);

			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("메일 작성 중 에러", e); // TODO: 메일 에러로 전환
		}
	}

	private MimeMessage getMessage(MailInfoToSendDto mailInfoDto) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		mimeMessage.addRecipients(Message.RecipientType.TO, mailInfoDto.getUserEmail());
		mimeMessage.setFrom(email);

		return mimeMessage;
	}
}

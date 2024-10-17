package org.c4marathon.assignment.mail.application;

import org.c4marathon.assignment.mail.dto.UserInfoDto;
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

	public void sendAccountCreateMsg(UserInfoDto userInfo) throws MessagingException {
		MimeMessage message = getMessage(userInfo);
		message.setSubject("계좌 생성이 완료되었습니다!");
		message.setText(userInfo.nickname() + "님, 계좌 생성이 완료되었습니다!");
	}

	private MimeMessage getMessage(UserInfoDto userInfo) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		mimeMessage.addRecipients(Message.RecipientType.TO, userInfo.email());
		mimeMessage.setFrom(email);

		return mimeMessage;
	}
}

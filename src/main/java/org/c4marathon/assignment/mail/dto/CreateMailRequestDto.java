package org.c4marathon.assignment.mail.dto;

import java.time.LocalDateTime;

import org.c4marathon.assignment.mail.domain.Mail;

public record CreateMailRequestDto(int userId, int accountId) {
	public Mail toMail() {
		return Mail.builder()
			.userId(userId)
			.accountId(accountId)
			.requestTime(LocalDateTime.now())
			.build();
	}
}

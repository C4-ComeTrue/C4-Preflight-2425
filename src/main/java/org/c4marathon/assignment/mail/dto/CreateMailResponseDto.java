package org.c4marathon.assignment.mail.dto;

import org.c4marathon.assignment.mail.domain.Mail;

public record CreateMailResponseDto(long mailId) {
	public static CreateMailResponseDto from(Mail mail) {
		return new CreateMailResponseDto(mail.getId());
	}
}

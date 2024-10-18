package org.c4marathon.assignment.mail.dto;

public interface MailInfoToSendDto {
	long getMailId();
	String getAccountNumber();
	String getUserEmail();
	String getUserNickname();
}

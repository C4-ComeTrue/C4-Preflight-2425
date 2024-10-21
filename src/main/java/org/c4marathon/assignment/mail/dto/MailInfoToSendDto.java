package org.c4marathon.assignment.mail.dto;

public interface MailInfoToSendDto {
	long getMailId();
	String getAccountNumber();
	String getUserPreEmail();
	String getUserRecentEmail();
	String getUserNickname();
}

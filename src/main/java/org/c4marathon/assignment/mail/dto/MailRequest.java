package org.c4marathon.assignment.mail.dto;

public record MailRequest(
        Long accountId,
        String email,
        String content
) {
}

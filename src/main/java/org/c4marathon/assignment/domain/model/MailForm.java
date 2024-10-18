package org.c4marathon.assignment.domain.model;

public record MailForm(
        Integer mailLogId,
        String to,
        String subject,
        String content
) {
}
package org.c4marathon.assignment.model;

import lombok.Getter;

@Getter
public record MailForm(
        String to,
        String subject,
        String content
) {
}
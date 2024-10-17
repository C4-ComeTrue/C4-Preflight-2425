package org.c4marathon.assignment.mail.dto;

import java.time.LocalDateTime;

public record MailRequest(
        Long accountId,
        String email,
        String content
) {
}

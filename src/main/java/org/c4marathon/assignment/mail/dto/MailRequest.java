package org.c4marathon.assignment.mail.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MailRequest(
        @NotNull
        Long accountId,

        @NotNull
        String email,

        @NotNull
        @Size(min = 10, max = 500)
        String content
) {
}

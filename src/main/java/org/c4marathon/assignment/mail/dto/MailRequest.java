package org.c4marathon.assignment.mail.dto;

import jakarta.validation.constraints.*;

public record MailRequest(
        @NotNull
        @Positive
        Long accountId,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 10, max = 500)
        String content
) {
}

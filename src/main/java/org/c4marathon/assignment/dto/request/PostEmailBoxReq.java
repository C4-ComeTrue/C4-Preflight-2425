package org.c4marathon.assignment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PostEmailBoxReq(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String accountDescription
) {
}

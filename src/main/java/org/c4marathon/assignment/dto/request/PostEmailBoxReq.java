package org.c4marathon.assignment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;

public record PostEmailBoxReq(
        @NotBlank
        @Email
        String email,

        @Max(500)
        @NotBlank
        String accountDescription
) {
}
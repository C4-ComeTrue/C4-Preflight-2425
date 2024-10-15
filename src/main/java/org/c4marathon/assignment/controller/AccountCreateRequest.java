package org.c4marathon.assignment.controller;

import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
        @Size(max = 13)
        String accountNumber,
        int userId,
        char accountType,
        @Size(max = 200)
        String memo
) {
}

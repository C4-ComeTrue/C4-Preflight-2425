package org.c4marathon.assignment.controller.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record DateRequest(
        @NotBlank
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Instant date
) {
}

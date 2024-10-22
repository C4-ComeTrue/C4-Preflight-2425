package org.c4marathon.assignment.controller.response;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

public record CumulativeAmountResponse(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Instant startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        Instant endDate,
        int dailyAmount,
        long cumulativeAmount
) {
}

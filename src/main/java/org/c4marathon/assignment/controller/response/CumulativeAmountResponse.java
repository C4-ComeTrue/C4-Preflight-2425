package org.c4marathon.assignment.controller.response;

import org.c4marathon.assignment.domain.CumulativeAmount;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CumulativeAmountResponse(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        long dailyAmount,
        long cumulativeAmount
) {
        public CumulativeAmountResponse(CumulativeAmount cumulativeAmount) {
                this(
                        cumulativeAmount.getDate(),
                        cumulativeAmount.getDate(),
                        cumulativeAmount.getDailyAmount(),
                        cumulativeAmount.getCumulativeAmount()
                );
        }
}

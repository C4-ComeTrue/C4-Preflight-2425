package org.c4marathon.assignment.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.c4marathon.assignment.domain.CumulativeAmount;

import java.time.LocalDate;

public record CumulativeAmountResponse(
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate startDate,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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

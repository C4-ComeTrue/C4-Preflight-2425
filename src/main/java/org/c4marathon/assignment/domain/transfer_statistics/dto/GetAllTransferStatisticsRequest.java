package org.c4marathon.assignment.domain.transfer_statistics.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

public record GetAllTransferStatisticsRequest(
	@NotBlank
	@Past
	LocalDate startDate,
	@NotBlank
	@Past
	LocalDate endDate
) {
	@AssertTrue(message = "시작 날짜는 종료 날짜보다 이전이어야 합니다.")
	public boolean isStartDateBeforeEndDate() {
		return startDate.isBefore(endDate);
	}
}

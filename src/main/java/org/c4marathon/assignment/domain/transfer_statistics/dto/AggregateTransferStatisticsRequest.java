package org.c4marathon.assignment.domain.transfer_statistics.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

public record AggregateTransferStatisticsRequest(
	@NotBlank
	@PastOrPresent
	LocalDateTime targetDate
) {
}

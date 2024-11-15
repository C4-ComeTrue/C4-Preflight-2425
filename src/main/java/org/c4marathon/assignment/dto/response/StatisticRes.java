package org.c4marathon.assignment.dto.response;

import java.time.Instant;

import org.c4marathon.assignment.entity.Statistic;

public record StatisticRes(
	Long id,
	Long totalRemittance,
	Long cumulativeRemittance,
	Instant statisticDate
) {
	public StatisticRes(Statistic statistic) {
		this(
			statistic.getId(),
			statistic.getTotalRemittance(),
			statistic.getCumulativeRemittance(),
			statistic.getStatisticDate()
		);
	}
}

package org.c4marathon.assignment.statistics.dto;

import org.c4marathon.assignment.statistics.domain.Statistics;

import java.time.LocalDate;

public record StatisticsResponse(
        LocalDate statisticsDate,
        Long totalRemittance,
        Long cumulativeRemittance
) {
    public StatisticsResponse(Statistics statistics) {
        this(
                statistics.getStatisticsDate(),
                statistics.getTotalRemittance(),
                statistics.getCumulativeRemittance()
        );
    }
}


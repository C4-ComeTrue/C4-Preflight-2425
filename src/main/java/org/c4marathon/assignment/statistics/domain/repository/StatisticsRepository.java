package org.c4marathon.assignment.statistics.domain.repository;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.statistics.domain.Statistics;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatisticsRepository {
    private final StatisticsJpaRepository statisticsJpaRepository;

    public Long getLatestCumulativeRemittance() {
        Long latestCumulativeRemittance = statisticsJpaRepository.getLatestCumulativeRemittance();
        return latestCumulativeRemittance != null ? latestCumulativeRemittance : 0L;
    }

    public Statistics findByStatisticsDate(LocalDate transactionDate) {
        return statisticsJpaRepository.findByStatisticsDate(transactionDate);
    }

    public void save(Statistics statistics) {
        statisticsJpaRepository.save(statistics);
    }

    public List<Statistics> findByStatisticsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        return statisticsJpaRepository.findByStatisticsByStartDateAndEndDate(startDate, endDate);
    }
}


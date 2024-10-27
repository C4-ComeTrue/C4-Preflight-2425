package org.c4marathon.assignment.statistics.domain.repository;

import org.c4marathon.assignment.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long> {

    // index(statisticsDate)
    @Query("""
    SELECT s.cumulativeRemittance
    FROM Statistics s
    ORDER BY s.statisticsDate DESC
    LIMIT 1
    """)
    Long getLatestCumulativeRemittance();

    // index(statisticsDate)
    @Query("""
    SELECT s
    FROM Statistics s
    WHERE s.statisticsDate = :transactionDate
    """)
    Statistics findByStatisticsDate(@Param("transactionDate") LocalDate transactionDate);

    // index(statisticsDate)
    @Query("""
    SELECT s
    FROM Statistics s
    WHERE s.statisticsDate BETWEEN :startDate AND :endDate
    """)
    List<Statistics> findByStatisticsByStartDateAndEndDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

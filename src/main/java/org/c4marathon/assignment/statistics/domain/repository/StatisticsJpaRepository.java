package org.c4marathon.assignment.statistics.domain.repository;

import org.c4marathon.assignment.statistics.domain.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StatisticsJpaRepository extends JpaRepository<Statistics, Long> {

    @Query("""
    SELECT s.cumulativeRemittance
    FROM Statistics s
    ORDER BY s.statisticsDate DESC
    LIMIT 1
    """)
    Long getLatestCumulativeRemittance();

    @Query("""
    SELECT s
    FROM Statistics s
    WHERE s.statisticsDate = :transactionDate
    """)
    Statistics findByStatisticsDate(@Param("transactionDate") LocalDate transactionDate);
}

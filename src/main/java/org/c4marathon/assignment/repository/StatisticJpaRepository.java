package org.c4marathon.assignment.repository;

import java.time.Instant;
import java.util.List;

import org.c4marathon.assignment.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatisticJpaRepository extends JpaRepository<Statistic, Long> {
	@Query("""
		SELECT s
		FROM Statistic s WHERE :startDate <= s.statisticDate AND s.statisticDate < :endDate
		""")
	List<Statistic> findAllByStatisticDateBetween(Instant startDate, Instant endDate);
}

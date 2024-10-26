package org.c4marathon.assignment.statistic.infrastructure;

import java.time.Instant;
import java.util.Optional;

import org.c4marathon.assignment.statistic.domain.TransactionStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionStatisticJpaRepository extends JpaRepository<TransactionStatistic, Long> {

	@Query(value = """
		SELECT *
		FROM transaction_statistic_zzamba ts
		WHERE ts.statistic_date <= :theDay
		ORDER BY ts.statistic_date DESC
		LIMIT 1
	""", nativeQuery = true)
	Optional<TransactionStatistic> findCloseStatisticByStatisticDate(Instant theDay);
}

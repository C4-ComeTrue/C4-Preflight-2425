package org.c4marathon.assignment.statistic.domain;

import java.time.Instant;
import java.util.Optional;

import org.c4marathon.assignment.statistic.infrastructure.TransactionStatisticJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionStatisticRepository {
	private final TransactionStatisticJpaRepository statisticJpaRepository;

	public TransactionStatisticRepository(TransactionStatisticJpaRepository statisticJpaRepository) {
		this.statisticJpaRepository = statisticJpaRepository;
	}

	public Optional<TransactionStatistic> findCloseStatisticByStatisticDate(Instant theDay) {
		return statisticJpaRepository.findCloseStatisticByStatisticDate(theDay);
	}

	public void save(TransactionStatistic transactionStatistic) {
		statisticJpaRepository.save(transactionStatistic);
	}
}

package org.c4marathon.assignment.statistic.domain;

import org.c4marathon.assignment.statistic.infrastructure.TransactionStatisticJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionStatisticRepository {
	private final TransactionStatisticJpaRepository statisticJpaRepository;

	public TransactionStatisticRepository(TransactionStatisticJpaRepository statisticJpaRepository) {
		this.statisticJpaRepository = statisticJpaRepository;
	}


}

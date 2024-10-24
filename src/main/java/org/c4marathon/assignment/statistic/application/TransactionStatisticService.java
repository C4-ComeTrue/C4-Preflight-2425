package org.c4marathon.assignment.statistic.application;

import org.c4marathon.assignment.statistic.domain.TransactionStatisticRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatisticService {
	private final TransactionStatisticRepository statisticRepository;

	public TransactionStatisticService(TransactionStatisticRepository statisticRepository) {
		this.statisticRepository = statisticRepository;
	}


}

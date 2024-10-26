package org.c4marathon.assignment.statistic.dto;

import java.time.LocalDate;

import org.c4marathon.assignment.statistic.domain.TransactionStatistic;

public record TransactionStatisticResult(LocalDate date,
										 long dayAmount,
										 long cumulativeAmount) {

	public static TransactionStatisticResult from(TransactionStatistic transactionStatistic) {
		long epochDay = transactionStatistic.getStatisticDate().getEpochSecond() / (60L * 60L * 24L);

		return new TransactionStatisticResult(
			LocalDate.ofEpochDay(epochDay),
			transactionStatistic.getDailyTotalAmount(),
			transactionStatistic.getCumulativeAmount()
		);
	}
}

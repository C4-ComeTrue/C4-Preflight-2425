package org.c4marathon.assignment.statistic.dto;

import java.time.LocalDate;

import org.c4marathon.assignment.statistic.domain.TransactionStatistic;

public record TransactionStatisticResult(LocalDate date,
										 long dayAmount,
										 long cumulativeAmount) {

	public static TransactionStatisticResult from(TransactionStatistic transactionStatistic) {
		long epochSecond = transactionStatistic.getStatisticDate().getEpochSecond();

		return new TransactionStatisticResult(
			LocalDate.ofEpochDay(epochSecond),
			transactionStatistic.getDailyTotalAmount(),
			transactionStatistic.getCumulativeAmount()
		);
	}
}

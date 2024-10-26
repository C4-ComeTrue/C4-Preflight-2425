package org.c4marathon.assignment.statistic.dto;

import java.time.LocalDate;
import java.time.ZoneId;

import org.c4marathon.assignment.statistic.domain.TransactionStatistic;

public record TransactionStatisticResult(LocalDate date, long dayAmount, long cumulativeAmount) {

	public static TransactionStatisticResult from(TransactionStatistic transactionStatistic) {
		return new TransactionStatisticResult(
			LocalDate.ofInstant(transactionStatistic.getStatisticDate(), ZoneId.of("Z")),
			transactionStatistic.getDailyTotalAmount(),
			transactionStatistic.getCumulativeAmount()
		);
	}
}

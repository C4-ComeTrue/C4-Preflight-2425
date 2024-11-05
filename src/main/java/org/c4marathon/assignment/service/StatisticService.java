package org.c4marathon.assignment.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.c4marathon.assignment.dto.response.StatisticRes;
import org.c4marathon.assignment.entity.Statistic;
import org.c4marathon.assignment.repository.StatisticRepository;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {
	private final StatisticRepository statisticRepository;
	private final TransactionRepository transactionRepository;

	public List<StatisticRes> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
		Instant startInstantDate = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstantDate = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		return statisticRepository.findAllByStatisticDateBetween(startInstantDate, endInstantDate)
			.stream()
			.map(StatisticRes::new)
			.toList();
	}

	public StatisticRes recalculateStatistic(LocalDate date) {
		Instant startInstantDate = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstantDate = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		Long totalRemittance = transactionRepository.sumAmountBetweenDate(startInstantDate, endInstantDate);
		Long cumulativeRemittanceBeforeDate = transactionRepository.sumCumulativeRemittanceBeforeDate(startInstantDate);

		Statistic statistic = statisticRepository.findByStatisticDate(startInstantDate, endInstantDate)
			.orElseGet(() -> new Statistic(startInstantDate));

		statistic.changeStatistic(
			totalRemittance, cumulativeRemittanceBeforeDate + totalRemittance);

		return new StatisticRes(statisticRepository.save(statistic));
	}
}

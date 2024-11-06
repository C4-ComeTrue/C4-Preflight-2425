package org.c4marathon.assignment.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.c4marathon.assignment.dto.response.StatisticRes;
import org.c4marathon.assignment.entity.Statistic;
import org.c4marathon.assignment.repository.StatisticRepository;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {
	private final StatisticRepository statisticRepository;
	private final TransactionRepository transactionRepository;

	/**
	 * 시작 날짜와 종료 날짜를 받아 통계 데이터 조회한다.
	 * @param startDate
	 * @param endDate
	 */
	public List<StatisticRes> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
		Instant startInstantDate = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstantDate = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		return statisticRepository.findAllByStatisticDateBetween(startInstantDate, endInstantDate)
			.stream()
			.map(StatisticRes::new)
			.toList();
	}

	/**
	 * 전날까지의 누적 송금액과 당일 총 송금액을 구한 후 통계 데이터를 업데이트한다.
	 * @param date
	 */
	public StatisticRes recalculateStatistic(LocalDate date) {
		Instant startInstantDate = date.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstantDate = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		Long totalRemittance = transactionRepository.sumAmountBetweenDate(startInstantDate, endInstantDate);
		Long cumulativeRemittanceBeforeDate = transactionRepository.cumulativeRemittanceBeforeDate(startInstantDate);

		Statistic statistic = statisticRepository.findByStatisticDate(startInstantDate, endInstantDate)
			.orElseGet(() -> new Statistic(startInstantDate));

		statistic.changeStatistic(
			totalRemittance, cumulativeRemittanceBeforeDate + totalRemittance);

		return new StatisticRes(statisticRepository.save(statistic));
	}

	/**
	 * 새벽 4시마다 전날까지의 누적 송금액과 당일 총 송금액을 구한 후 통계 데이터를 업데이트한다.
	 */
	@Scheduled(cron = "0 0 4 * * *")
	public void calculateYesterdayStatistic() {
		LocalDate yesterday = LocalDate.now().minusDays(1);

		recalculateStatistic(yesterday);
	}
}

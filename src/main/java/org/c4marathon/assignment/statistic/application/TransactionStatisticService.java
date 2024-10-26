package org.c4marathon.assignment.statistic.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.c4marathon.assignment.global.util.QueryTemplate;
import org.c4marathon.assignment.statistic.domain.TransactionStatistic;
import org.c4marathon.assignment.statistic.domain.TransactionStatisticRepository;
import org.c4marathon.assignment.statistic.dto.TransactionStatisticResult;
import org.c4marathon.assignment.transaction.domain.Transaction;
import org.c4marathon.assignment.transaction.domain.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionStatisticService {
	private static final long DAY_TIME_SECONDS = 24L * 60L * 60L;
	private static final long SPLIT_SECONDS_PER_THREADS = 6L * 60L * 60L; // 스레드 별 시간 단위
	private static final int BATCH_SIZE = 1000;

	private final TransactionStatisticRepository statisticRepository;
	private final TransactionRepository transactionRepository;
	private final TransactionStatisticRepository transactionStatisticRepository;

	public TransactionStatisticService(TransactionStatisticRepository statisticRepository,
		TransactionRepository transactionRepository, TransactionStatisticRepository transactionStatisticRepository) {
		this.statisticRepository = statisticRepository;
		this.transactionRepository = transactionRepository;
		this.transactionStatisticRepository = transactionStatisticRepository;
	}

	public List<TransactionStatisticResult> findAll(Instant start, Instant end) {
		List<TransactionStatistic> statistics = transactionStatisticRepository.findAll(start, end);

		return statistics.stream().map(TransactionStatisticResult::from).toList();
	}

	/**
	 * theDay까지(해당 일 23:59:59까지) 통계를 집계합니다.
	 * @param theDay
	 * @return
	 */
	public TransactionStatisticResult aggregate(Instant theDay) {
		log.info("theDay: {}", theDay);
		Optional<TransactionStatistic> statisticOptional = statisticRepository.findCloseStatisticByStatisticDate(theDay);

		if (statisticOptional.isPresent() && Objects.equals(statisticOptional.get().getStatisticDate(), theDay)) {
			return TransactionStatisticResult.from(statisticOptional.get());
		}

		Instant endDay = getMidnightOfDay(theDay).plus(1L, ChronoUnit.DAYS);
		long cumulativeAmount = 0L;
		Instant startDay;

		if (statisticOptional.isPresent()) {
			TransactionStatistic transactionStatistic = statisticOptional.get();
			startDay = transactionStatistic.getStatisticDate().plus(1L, ChronoUnit.DAYS);
			cumulativeAmount = transactionStatistic.getCumulativeAmount();
		} else {
			Transaction firstTransaction = transactionRepository.findFirstOrderByTransactionDate().orElse(null);
			startDay = firstTransaction == null ? Instant.now() : getMidnightOfDay(firstTransaction.getTransactionDate());
		}

		List<Instant> splitTimes = getSplitTimes(startDay, endDay);

		Map<Instant, AtomicLong> statisticMap = new TreeMap<>(Comparator.comparing(Instant::getEpochSecond));
		splitTimes.forEach(splitTime -> statisticMap.putIfAbsent(getMidnightOfDay(splitTime), new AtomicLong()));

		ExecutorService executorService = Executors.newFixedThreadPool(8);

		splitTimes.forEach(splitTime -> executorService.execute(() ->
			QueryTemplate.<Transaction>selectAndExecuteWithCursor(BATCH_SIZE,
				transaction -> transactionRepository.findBetweenTimesWithCursor(transaction == null ? splitTime : transaction.getTransactionDate(), splitTime.plusSeconds(SPLIT_SECONDS_PER_THREADS), transaction == null ? null : transaction.getId(), BATCH_SIZE),
				transactions -> transactions.forEach(transaction -> calculateStatistic(transaction, statisticMap)))
			)
		);

		executorService.shutdown();
		try {
			executorService.awaitTermination(20L, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.warn("스레드 풀이 정상적으로 종료되지 않았습니다.");
		}

		List<TransactionStatistic> statistics = getStatistics(statisticMap, cumulativeAmount);

		transactionStatisticRepository.saveAll(statistics);

		return TransactionStatisticResult.from(statistics.get(statistics.size() - 1));
	}

	private void calculateStatistic(Transaction transaction, Map<Instant, AtomicLong> statisticMap) {
		Instant curTransactionDate = getMidnightOfDay(transaction.getTransactionDate());

		statisticMap.get(curTransactionDate).addAndGet(transaction.getAmount());
	}

	private Instant getMidnightOfDay(Instant time) {
		return time.minusSeconds(time.getEpochSecond() % DAY_TIME_SECONDS);
	}

	private List<Instant> getSplitTimes(Instant startTime, Instant endDay) {
		List<Instant> splitTimes = new ArrayList<>();

		while (startTime.isBefore(endDay)) {
			splitTimes.add(startTime);
			startTime = startTime.plusSeconds(SPLIT_SECONDS_PER_THREADS);
		}

		return splitTimes;
	}

	private List<TransactionStatistic> getStatistics(Map<Instant, AtomicLong> statisticMap, long cumulativeAmount) {
		List<TransactionStatistic> statistics = new ArrayList<>();
		for (var entry : statisticMap.entrySet()) {
			cumulativeAmount += entry.getValue().get();

			TransactionStatistic transactionStatistic = TransactionStatistic.builder()
				.statisticDate(entry.getKey())
				.dailyTotalAmount(entry.getValue().get())
				.cumulativeAmount(cumulativeAmount)
				.build();

			statistics.add(transactionStatistic);
		}

		return statistics;
	}

	@Scheduled(cron = "0 0 4 * * *")
	private void calculateStatistic() {
		aggregate(Instant.now().plus(9L, ChronoUnit.HOURS));
	}
}

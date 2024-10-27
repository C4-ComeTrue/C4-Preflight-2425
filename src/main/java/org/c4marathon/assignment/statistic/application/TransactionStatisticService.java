package org.c4marathon.assignment.statistic.application;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.c4marathon.assignment.global.util.CustomThreadPoolExecutor;
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
	private static final long SPLIT_SECONDS_PER_THREADS = 6L * 60L * 60L; // 스레드 별 시간 단위
	private static final int BATCH_SIZE = 1000;

	private final CustomThreadPoolExecutor executorService;
	private final TransactionRepository transactionRepository;
	private final TransactionStatisticRepository transactionStatisticRepository;

	public TransactionStatisticService(CustomThreadPoolExecutor executorService,
		TransactionRepository transactionRepository, TransactionStatisticRepository transactionStatisticRepository) {
		this.executorService = executorService;
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
		AtomicLong dailyAmount = new AtomicLong(0L);
		AtomicLong cumulativeAmount = new AtomicLong(0L);

		Instant endDay = getMidnightOfDay(theDay).plus(1L, ChronoUnit.DAYS);

		Instant startDate = Instant.EPOCH.plus(30L * 365L, ChronoUnit.DAYS);
		List<Instant> splitTimes = getSplitTimes(startDate, endDay);

		executorService.init();

		splitTimes.forEach(
			splitTime -> executorService.execute(() -> QueryTemplate.<Transaction>selectAndExecuteWithCursor(BATCH_SIZE,
					transaction -> transactionRepository.findBetweenTimesWithCursor(
						transaction == null ? splitTime : transaction.getTransactionDate(),
						splitTime.plusSeconds(SPLIT_SECONDS_PER_THREADS), transaction == null ? null : transaction.getId(),
						BATCH_SIZE),
					transactions -> transactions.forEach(
						transaction -> addAmount(dailyAmount, cumulativeAmount, transaction, theDay)
					)
				)
			));

		executorService.waitToEnd();

		TransactionStatistic statistic = TransactionStatistic.builder()
			.statisticDate(getMidnightOfDay(theDay))
			.dailyTotalAmount(dailyAmount.get())
			.cumulativeAmount(cumulativeAmount.get())
			.build();

		transactionStatisticRepository.save(statistic);

		return TransactionStatisticResult.from(statistic);
	}


	// 해당 날짜의 자정 시간을 반환. ex) 2023-01-01T22:02:41 -> 2023-01-01T00:00:00
	private Instant getMidnightOfDay(Instant time) {
		return Instant.ofEpochSecond(time.atZone(ZoneOffset.UTC).toLocalDate().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC));
	}

	// 통계할 시간을 일정 크기만큼 분할
	private List<Instant> getSplitTimes(Instant startTime, Instant endDay) {
		List<Instant> splitTimes = new ArrayList<>();

		while (startTime.isBefore(endDay)) {
			splitTimes.add(startTime);
			startTime = startTime.plusSeconds(SPLIT_SECONDS_PER_THREADS);
		}

		return splitTimes;
	}

	// 거래 내역의 날짜가 집계하는 날짜와 같으면 dailyAmount에 추가하고 아니면 cumulativeAmount에만 추가합니다.
	private void addAmount(AtomicLong dailyAmount, AtomicLong cumulativeAmount, Transaction transaction, Instant theDay) {
		if (getMidnightOfDay(transaction.getTransactionDate()).equals(getMidnightOfDay(theDay))) {
			dailyAmount.addAndGet(transaction.getAmount());
		}

		cumulativeAmount.addAndGet(transaction.getAmount());
	}

	@Scheduled(cron = "0 0 4 * * *")
	private void calculateStatistic() {
		aggregate(Instant.now().plus(9L, ChronoUnit.HOURS));
	}
}

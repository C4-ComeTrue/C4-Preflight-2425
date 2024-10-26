package org.c4marathon.assignment.statistic.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.c4marathon.assignment.global.util.CustomThreadPoolExecutor;
import org.c4marathon.assignment.global.util.QueryTemplate;
import org.c4marathon.assignment.statistic.domain.TransactionStatistic;
import org.c4marathon.assignment.statistic.domain.TransactionStatisticRepository;
import org.c4marathon.assignment.statistic.dto.TransactionStatisticResult;
import org.c4marathon.assignment.transaction.domain.Transaction;
import org.c4marathon.assignment.transaction.domain.TransactionRepository;
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
	private final CustomThreadPoolExecutor threadPoolExecutor;
	private final TransactionStatisticRepository transactionStatisticRepository;

	public TransactionStatisticService(TransactionStatisticRepository statisticRepository,
		TransactionRepository transactionRepository, CustomThreadPoolExecutor threadPoolExecutor,
		TransactionStatisticRepository transactionStatisticRepository) {
		this.statisticRepository = statisticRepository;
		this.transactionRepository = transactionRepository;
		this.threadPoolExecutor = threadPoolExecutor;
		this.transactionStatisticRepository = transactionStatisticRepository;
	}

	public TransactionStatisticResult aggregate(Instant theDay) {
		// 0. 통계 테이블에서 집계하려는 날짜의 데이터가 있으면 그걸 반환.
		Optional<TransactionStatistic> statisticOptional = statisticRepository.findCloseStatisticByStatisticDate(theDay);

		if (statisticOptional.isPresent() && Objects.equals(statisticOptional.get().getStatisticDate(), theDay)) {
			return TransactionStatisticResult.from(statisticOptional.get());
		}

		// 1. 통계 테이블에서 그 날짜와 제일 가까운 통계 있는지 확인.
		// 1-1. 없으면 트랜잭션 테이블 제일 첫 거래 날짜를 가져온다. -> 다음날 자정으로 기록한다.
		// 		근데 자정을 어떻게 설정할 수 있을까 -> toEpochSecond()로 표현한 뒤, % (3600 * 24)로 나온 값을 짜르자!
		// 1-1-1 트랜잭션도 없으면 그냥 지금 시간으로 기록한다(가져올 데이터 없도록)
		// 1-2. 있으면 그것을 가져오고 날짜와 누적 금액에 추가한다.

		Instant endDay = getMidnightOfDay(theDay).plus(1L, ChronoUnit.DAYS);
		AtomicLong theDayAmount = new AtomicLong(0L);
		AtomicLong cumulativeAmount = new AtomicLong(0L);
		Instant startDay;

		if (statisticOptional.isPresent()) {
			TransactionStatistic transactionStatistic = statisticOptional.get();
			startDay = transactionStatistic.getStatisticDate().plus(1L, ChronoUnit.DAYS);
			cumulativeAmount.addAndGet(transactionStatistic.getCumulativeAmount());
		} else {
			Transaction firstTransaction = transactionRepository.findFirstOrderByTransactionDate().orElse(null);
			startDay = firstTransaction == null ? Instant.now() : getMidnightOfDay(firstTransaction.getTransactionDate());
		}

		// 2. theDay 전까지 스레드 풀 각 스레드만큼 3시간 단위(비교하며 적절히 쪼개본다)로 분리하고, 배치로 Transaction 테이블 돌면서 다음을 수행
		// 2-1. cumulativeAmount에 추가
		// 2-2. 현재 Instant가 (theDay 그 자정)보다 같거나 크면 theDayAmount에 추가

		List<Instant> splitTimes = getSplitTimes(startDay, endDay);

		ExecutorService executorService = Executors.newFixedThreadPool(8);

		splitTimes.forEach(splitTime -> executorService.execute(() ->
			QueryTemplate.<Transaction>selectAndExecuteWithCursor(BATCH_SIZE,
				transaction -> transactionRepository.findBetweenTimesWithCursor(transaction == null ? splitTime : transaction.getTransactionDate(), splitTime.plusSeconds(SPLIT_SECONDS_PER_THREADS), transaction == null ? null : transaction.getId(), BATCH_SIZE),
				transactions -> transactions.forEach(transaction -> calculateStatistic(theDay, transaction, cumulativeAmount, theDayAmount)))
			)
		);

		//threadPoolExecutor.waitToEnd();
		executorService.shutdown();
		try {
			executorService.awaitTermination(1L, TimeUnit.HOURS);
		} catch (Exception e) {
			log.warn("스레드 풀이 정상적으로 종료되지 않았습니다.");
		}

		TransactionStatistic transactionStatistic = TransactionStatistic.builder()
			.dailyTotalAmount(theDayAmount.get())
			.cumulativeAmount(cumulativeAmount.get())
			.statisticDate(getMidnightOfDay(theDay))
			.build();

		transactionStatisticRepository.save(transactionStatistic);

		return TransactionStatisticResult.from(transactionStatistic);
	}

	private void calculateStatistic(Instant theDay, Transaction transaction, AtomicLong cumulativeAmount,
		AtomicLong theDayAmount) {
		cumulativeAmount.addAndGet(transaction.getAmount());

		if (isSameDay(transaction.getTransactionDate(), theDay)) {
			theDayAmount.addAndGet(transaction.getAmount());
		}
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

	private boolean isSameDay(Instant instant1, Instant instant2) {
		return getMidnightOfDay(instant1).equals(getMidnightOfDay(instant2));
	}
}

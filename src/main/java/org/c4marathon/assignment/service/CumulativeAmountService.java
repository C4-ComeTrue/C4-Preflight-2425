package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.c4marathon.assignment.controller.response.CumulativeAmountResponse;
import org.c4marathon.assignment.domain.CumulativeAmount;
import org.c4marathon.assignment.domain.Transaction;
import org.c4marathon.assignment.repository.CumulativeRepository;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.c4marathon.assignment.util.C4QueryExecuteTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class CumulativeAmountService {
	private final TransactionRepository transactionRepository;
	private final CumulativeRepository cumulativeRepository;

	/**
	 * [1] API : statics/cumulative
	 * transaction의 ~ date(endDate) 까지의 일별/누적금액 강제로 무조건 계산하는 로직
	 */
	public CumulativeAmountResponse getCumulativeAmountDate(LocalDate date) {

		Instant endDate = date.atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();
		Map<LocalDate, Long> accumulatedMap = new HashMap<>(); //배치 일괄처리용 맵

		C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(
			-1, 1000,
			lastTransaction -> transactionRepository.findTransactionUntilDate(
				endDate,
				lastTransaction == null ? null : lastTransaction.getTransactionDate(),
				lastTransaction == null ? null : lastTransaction.getId(),
				1000
			),
			transactionList -> calDailyAmountByDate(transactionList, accumulatedMap)
		);

		batchUpdateCumulativeAmounts(accumulatedMap);

		CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date)
			.orElseGet(() -> new CumulativeAmount(date, 0L, 0L));

		return new CumulativeAmountResponse(cumulativeAmount);
	}

	/**
	 * [2] API : statics/cumulative/range
	 * start ~ endDate 사이의 날짜로 테이블 단순 조회
	 */
	public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(LocalDate startDate, LocalDate endDate) {

		List<CumulativeAmountResponse> responseList = new ArrayList<>();
		List<CumulativeAmount> cumulativeAmounts = cumulativeRepository.findByDateBetween(startDate,
			endDate.plusDays(1));

		LocalDate currentDate = startDate;
		while (!currentDate.isAfter(endDate)) {
			LocalDate finalCurrentDate = currentDate;
			CumulativeAmount matchingAmount = cumulativeAmounts.stream()
				.filter(cumulative -> cumulative.getDate().equals(finalCurrentDate))
				.findFirst()
				.orElse(null);

			// 만약에 통계 테이블이 비어있다면 0을 출력
			long dailyAmount = (matchingAmount != null) ? matchingAmount.getDailyAmount() : 0L;
			long cumulativeAmount = (matchingAmount != null) ? matchingAmount.getCumulativeAmount() : 0L;

			CumulativeAmountResponse response = new CumulativeAmountResponse(currentDate, currentDate, dailyAmount, cumulativeAmount);
			responseList.add(response);

			// 다음 날짜로 이동하며 값이 있는지 체크
			currentDate = currentDate.plusDays(1);
		}

		return responseList;
	}

	/**
	 * Transaction 거래 내역 List로 받아서 통계를 내는 로직
	 * */
	private void calDailyAmountByDate(List<Transaction> transactionList, Map<LocalDate, Long> accumulatedMap) {
		Map<LocalDate, Long> map = transactionList.stream()
			.collect(Collectors.groupingBy(
				transaction -> transaction.getTransactionDate().atZone(ZoneOffset.UTC).toLocalDate(),
				Collectors.summingLong(Transaction::getAmount)
			));

		map.forEach((date, amount) -> accumulatedMap.merge(date, amount, Long::sum));
	}

	/**
	 * 배치로 한번에 업데이트
	 * */
	private void batchUpdateCumulativeAmounts(Map<LocalDate, Long> accumulatedMap) {
		List<CumulativeAmount> cumulativeAmountsToUpdate = new ArrayList<>();

		// 날짜를 오름차순으로 정렬 - 안할 경우, 날짜가 반대로 저장이 되는 문제 발생
		List<LocalDate> sortedDates = accumulatedMap.keySet().stream().sorted().toList();
		long runningCumulativeAmount = 0L;

		for (LocalDate date : sortedDates) {
			long dailyAmount = accumulatedMap.get(date);
			runningCumulativeAmount += dailyAmount;

			CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date)
				.orElseGet(() -> new CumulativeAmount(date, 0L, 0L));

			cumulativeAmount.update(date,dailyAmount, runningCumulativeAmount);
			cumulativeAmountsToUpdate.add(cumulativeAmount);
		}

		cumulativeRepository.saveAll(cumulativeAmountsToUpdate);
	}

	/**
	 *  CumulativeAmount 객체를 생성하여 저장하는 메서드
	 */
	private void saveCumulativeAmount(LocalDate date, long dailyAmount, long cumulativeAmount) {
		CumulativeAmount cumulative = new CumulativeAmount(date, dailyAmount, cumulativeAmount);
		cumulativeRepository.save(cumulative);
	}

	/**
	 * [3] Scheduler
	 * 새벽 4시 마다, 전날의 통계 집계가 진행된다.
	 * - 주의할 점
	 * (1) 일별 누적과, 전체 누적은 매번 다시 수행한다.
	 * (2) 이전날의 값들에 의존하면 안된다
	 */
	@Scheduled(cron = "0 0 4 * * *")
	protected void cumulativeScheduler() {
		Instant yesterDay = LocalDate.now().minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant today = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant();

		long dailyAmount = transactionRepository.sumOfAmountByOneDate(yesterDay, today);
		long cumulativeAmount = transactionRepository.sumCumulativeAmountUntilDate(today);
		saveCumulativeAmount(LocalDate.now().minusDays(1), dailyAmount, cumulativeAmount);
	}

}

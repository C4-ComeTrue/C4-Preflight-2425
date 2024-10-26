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
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

		// 1. 해당 날짜의 통계 테이블이 있으면 return
		CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);
		if (cumulativeAmount != null) {
			return new CumulativeAmountResponse(cumulativeAmount);
		}
		calTransationAmount(date);

		return new CumulativeAmountResponse(cumulativeRepository.findByDate(date));
	}

	/**
	 * [2] API : statics/cumulative/range
	 * start ~ endDate 사이의 날짜로 테이블 단순 조회
	 */
	public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(LocalDate startDate, LocalDate endDate) {

		List<CumulativeAmountResponse> responseList = new ArrayList<>();
		List<CumulativeAmount> cumulativeAmounts = cumulativeRepository.findByDateBetween(startDate, endDate.plusDays(1));

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
	 * 주어진 날짜까지의 통계 저장하는 로직
	 * */
	private void calTransationAmount(LocalDate date) {
		//1. Transaction에 존재하는 첫날부터 ~date까지 계산 예정
		Instant earliestTransactionDate = transactionRepository.findEarliestTransactionDate(); //1월 1일
		LocalDate currentDate = earliestTransactionDate.atZone(ZoneOffset.UTC).toLocalDate(); //1월 1일

		//2.매일 하루치마다 dailyAmount 구하기
		while (!currentDate.isAfter(date)) {
			// 3. 해당 날짜에 이미 값이 존재하면 패스
			if (cumulativeRepository.findByDate(currentDate) != null) {
				currentDate = currentDate.plusDays(1);
				continue;
			}
			//4. 해당 일의 dailyAmount 값
			long dailyAmount = calculateDailyAmount(currentDate).get();

			//5. 만약 첫번째로 존재하는 날이라면 당연히 DB가 비어있을 테니 총 누적금액도 일별누적금액과 동일하다.
			if (currentDate.equals(earliestTransactionDate.atZone(ZoneOffset.UTC).toLocalDate())) {
				saveCumulativeAmount(currentDate, dailyAmount, dailyAmount);
			} else {
				//6. 그 이후로는 전날의 값을 조회하고 오늘의 일별 누적 금액 더해서 저장
				CumulativeAmount previousCA = cumulativeRepository.findByDate(currentDate.minusDays(1));
				long cumulativeTotal = previousCA.getCumulativeAmount() + dailyAmount;
				saveCumulativeAmount(currentDate, dailyAmount, cumulativeTotal);
			}
			//7. 다음날을 비교한다.
			currentDate = currentDate.plusDays(1);
		}
	}

	/**
	 * 하루치 트랜잭션 합산 (dailyAmount 계산)
	 */
	private AtomicLong calculateDailyAmount(LocalDate currentDate) {
		AtomicLong dailyAmount = new AtomicLong();
		Instant startOfDay = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		Instant endOfDay = currentDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();

		C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(-1, 1000,
			lastTransaction -> transactionRepository.findOneDayTransaction(startOfDay, endOfDay,
				lastTransaction == null ? null : lastTransaction.getTransactionDate(),
				lastTransaction == null ? null : lastTransaction.getId(), 1000),
			transactionList -> dailyAmount.addAndGet(transactionList.stream().mapToLong(Transaction::getAmount).sum()));

		return dailyAmount;
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
	 * - 새벽 4시 마다, 전날의 통계 집계가 진행된다.
	 * - 주의할 점
	 * 	 (1) 일별 누적과, 전체 누적은 매번 다시 수행한다.
	 *   (2) 이전날의 값들에 의존하면 안된다
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

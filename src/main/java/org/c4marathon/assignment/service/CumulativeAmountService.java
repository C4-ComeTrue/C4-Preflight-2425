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
	 * transaction의 ~ date(endDate) 까지의 일별/누적금액 계산하는 로직
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
	 * start ~ endDate 사이의 일별/누적 금액 다시 계산 하는 로직
	 * startDate ~ endDate 까지 오고간 통계금액이 필요하기 때문에, 누적금액의 단순 테이블 조회가 아닌 시작일을 기준으로 다시 누적금액을 계산했다.
	 * 예를들어, 1월 2일부터 5일까지면 1일의 누적금액은 당연히 빠져야한다.
	 */
	public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(LocalDate startDate, LocalDate endDate) {
		List<CumulativeAmountResponse> responses = new ArrayList<>();

		//1. 만약 시작날짜의 값이 없다면 - (statics/cumulative가 JAVA heap 메모리 용량이 적어서 OOM 발생으로 인해 값을 다 못채움)
		CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(endDate);
		if (cumulativeAmount == null) {
			//2. endDate까지 값을 다 채워 넣는다.
			calTransationAmount(endDate);
		}
		CumulativeAmount previousCA = cumulativeRepository.findByDate(startDate);
		long cumulativeTotal = previousCA.getDailyAmount();

		responses.add(new CumulativeAmountResponse(startDate, startDate, previousCA.getDailyAmount(), cumulativeTotal));

		LocalDate currentDate = startDate.plusDays(1);
		while (!currentDate.isAfter(endDate)) {
			CumulativeAmount currentCA = cumulativeRepository.findByDate(currentDate);
			long dailyAmount = currentCA.getDailyAmount();
			cumulativeTotal += dailyAmount;

			responses.add(new CumulativeAmountResponse(currentDate, currentDate, dailyAmount, cumulativeTotal));

			currentDate = currentDate.plusDays(1);
		}

		return responses;
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
	 * [3] Scheduler : 새벽 4시 마다, 전날의 통계 집계가 진행된다.
	 */
	@Scheduled(cron = "0 0 4 * * *")
	protected void cumulativeScheduler() {
		LocalDate yesterday = LocalDate.now().minusDays(1);
		long dailyAmount = calculateDailyAmount(yesterday).get();

		LocalDate twoDaysAgo = yesterday.minusDays(1);
		CumulativeAmount twoDaysAgoCA = cumulativeRepository.findByDate(twoDaysAgo);
		long cumulativeTotal = (twoDaysAgoCA != null ? twoDaysAgoCA.getCumulativeAmount() : 0L) + dailyAmount;

		saveCumulativeAmount(yesterday, dailyAmount, cumulativeTotal);
	}

}

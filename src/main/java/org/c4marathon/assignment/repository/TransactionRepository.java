package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.Transaction;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
	private final TransactionJpaRepository transactionJpaRepository;

	public Instant findEarliestTransactionDate() {
		return transactionJpaRepository.findEarliestTransactionDate();
	}

	/**
	 *  주어진 lastDate를 활용하여, size 만큼의 Transaction 정보를 가져온다.
	 *  @param lastDate : 페이징을 할때 1000개의 마지막 Date를 기반으로 다음 1000개의 순서보장
	 *  @param size : 페이징을 할 크기 단위
	 *  @return : List<Transaction>
	 */
	public List<Transaction> findOneDayTransaction(Instant startDate, Instant endDate, Instant lastDate,
		Integer lastDateId, int size) {

		if (lastDateId == null) {
			return transactionJpaRepository.findTransactionWithEndDate(startDate, endDate, size);
		}
		return transactionJpaRepository.findOneDayTransactionWithEndDateAndLastDate(startDate, endDate, lastDate,
			lastDateId, size);
	}


	/**
	 *  하루치에 대한 dailyAmount 계산 Full Scan
	 */
	public long sumOfAmountByOneDate(Instant startDate, Instant endDate) {
		return transactionJpaRepository.sumOfAmountByOneDate(startDate, endDate);
	}

	/**
	 *  date를 기준으로 이전날짜 까지릐 전체 Amount 계산
	 */
	public long sumCumulativeAmountUntilDate(Instant endDate) {
		return transactionJpaRepository.sumCumulativeAmountUntilDate(endDate);
	}

}

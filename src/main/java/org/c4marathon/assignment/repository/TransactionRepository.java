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

	/**
	 *  주어진 endDat전까지 모든 Transaction 정보를 size만큼씩 가져온다.
	 */
	public List<Transaction> findTransactionUntilDate(Instant endDate, Instant lastDate, Integer lastDateId, int size) {
		if (lastDateId == null) {
			return transactionJpaRepository.findTransactionUntilDate(endDate, size);
		}
		return transactionJpaRepository.findTransactionUntilDateWithPaging(endDate, lastDate, lastDateId, size);
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

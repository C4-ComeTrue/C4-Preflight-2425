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
	 *  @param lastDate
	 *  @param size
	 *  @return
	 */
	public List<Transaction> findOneDayTransaction(Instant startDate, Instant endDate, Instant lastDate, Integer lastDateId, int size){

		if(lastDateId == null){
			return transactionJpaRepository.findTransactionWithEndDate(startDate, endDate, size);
		}
		return transactionJpaRepository.findOneDayTransactionWithEndDateAndLastDate(startDate, endDate, lastDate, lastDateId, size);
	}

}

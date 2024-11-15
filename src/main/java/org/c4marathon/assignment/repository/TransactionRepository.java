package org.c4marathon.assignment.repository;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TransactionRepository {
	private final TransactionJpaRepository transactionJpaRepository;

	public Long sumAmountBetweenDate(Instant startInstantDate, Instant endInstantDate) {
		return transactionJpaRepository.sumAmountBetweenDate(startInstantDate, endInstantDate);
	}

	public Long cumulativeRemittanceBeforeDate(Instant instantDate) {
		return transactionJpaRepository.cumulativeRemittanceBeforeDate(instantDate);
	}
}

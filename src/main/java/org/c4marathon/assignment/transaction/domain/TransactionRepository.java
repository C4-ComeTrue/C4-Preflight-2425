package org.c4marathon.assignment.transaction.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.transaction.infrastructure.TransactionJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {
	private final TransactionJpaRepository transactionJpaRepository;

	public TransactionRepository(TransactionJpaRepository transactionJpaRepository) {
		this.transactionJpaRepository = transactionJpaRepository;
	}

	public Optional<Transaction> findFirstOrderByTransactionDate() {
		return transactionJpaRepository.findFirstOrderByTransactionDate();
	}

	public List<Transaction> findBetweenTimesWithoutCursor(Instant startTime, Instant endTime, int limit) {
		return transactionJpaRepository.findByTransactionDateBetween(startTime, endTime, limit);
	}

	public List<Transaction> findBetweenTimesWithCursor(Instant startTime, Instant endTime, Integer lastTransactionId, int limit) {
		if (lastTransactionId == null) {
			return findBetweenTimesWithoutCursor(startTime, endTime, limit);
		}

		return transactionJpaRepository.findByTransactionDateBetweenWithCursor(startTime, endTime, lastTransactionId, limit);
	}
}

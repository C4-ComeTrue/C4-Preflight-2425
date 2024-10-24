package org.c4marathon.assignment.transaction.domain;

import org.c4marathon.assignment.transaction.infrastructure.TransactionJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {
	private final TransactionJpaRepository transactionJpaRepository;

	public TransactionRepository(TransactionJpaRepository transactionJpaRepository) {
		this.transactionJpaRepository = transactionJpaRepository;
	}


}

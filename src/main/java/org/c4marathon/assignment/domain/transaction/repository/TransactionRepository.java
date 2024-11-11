package org.c4marathon.assignment.domain.transaction.repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.c4marathon.assignment.domain.transaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query(
		"""
			SELECT t FROM Transaction t 
			WHERE t.transactionType = :type AND t.createdDate = :date
			""")
	Stream<Transaction> findAllTransactionByTransactionTypeAndCreatedDate(TransactionType type, LocalDateTime date);
}
package org.c4marathon.assignment.domain.transaction.repository;

import java.util.List;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	List<Transaction> findAllBySenderAccountOrderByTransactionDateDesc(String senderAccount, Pageable pageable);
}

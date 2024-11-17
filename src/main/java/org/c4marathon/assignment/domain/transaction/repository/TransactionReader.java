package org.c4marathon.assignment.domain.transaction.repository;

import java.util.List;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionReader {
	private final TransactionRepository transactionRepository;

	public List<Transaction> findAllTransactionsBySenderName(String senderName, Pageable pageable) {
		return transactionRepository.findAllBySenderAccountOrderByTransactionDateDesc(senderName, pageable);
	}
}

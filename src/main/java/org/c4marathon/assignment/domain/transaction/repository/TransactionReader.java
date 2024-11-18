package org.c4marathon.assignment.domain.transaction.repository;

import java.util.List;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionReader {
	private final TransactionRepository transactionRepository;

	public List<Transaction> findAllTransactionsBySenderAccount(String senderAccount) {
		return transactionRepository.findAllBySenderAccountOrderByTransactionDateDesc(senderAccount);
	}
}

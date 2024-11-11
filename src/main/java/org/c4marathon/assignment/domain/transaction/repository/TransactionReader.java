package org.c4marathon.assignment.domain.transaction.repository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.c4marathon.assignment.domain.transaction.entity.TransactionType;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionReader {
	private final TransactionRepository transactionRepository;

	public Stream<Transaction> findAllTransfersByDate(LocalDateTime date) {
		return transactionRepository.findAllTransactionByTransactionTypeAndCreatedDate(TransactionType.TRANSFER, date);
	}
}

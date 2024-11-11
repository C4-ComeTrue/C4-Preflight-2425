package org.c4marathon.assignment.domain.transaction.repository;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionReader {
	private final TransactionRepository transactionRepository;

	public Stream<TransferStatistics> findTransferStatisticsBetweeen(LocalDate from, LocalDate to) {
		return transactionRepository.findTransactionBetween(from, to);
	}
}

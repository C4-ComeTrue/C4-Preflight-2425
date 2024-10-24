package org.c4marathon.assignment.transaction.domain.repository;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.transaction.domain.Transaction;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TransactionRepository {
    private final TransactionJpaRepository transactionJpaRepository;

    public List<Transaction> findTransactionByLastDate(LocalDate endDate, Instant lastDate, int lastId, int size) {
        if (lastDate == null) {
            return transactionJpaRepository.findTransaction(size);
        }
        return transactionJpaRepository.findTransactionWithEndDateAndLastDate(endDate, lastDate, lastId, size);
    }
}

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

    public List<Transaction> findTransactionByLastDate(LocalDate endDate, Instant transactionDate, int id, int size) {
        if (transactionDate == null) {
            return transactionJpaRepository.findTransaction(size);
        }
        return transactionJpaRepository.findTransactionWithEndDateAndLastDate(endDate, transactionDate, id, size);
    }

    public List<Transaction> findTransactionByDate(LocalDate endDate, Instant transactionDate, int id, int size) {
        if (transactionDate == null) {
            return transactionJpaRepository.findTransactionByDate(endDate, size);
        }
        return transactionJpaRepository.findTransactionWithEndDateAndLastDate(endDate, transactionDate, id, size);
    }

    public Long getAllTransactionAmountSum() {
        return transactionJpaRepository.findAllTransactionAmountSum();
    }

    public Long getAllTransactionAmountSumBeforeDate(LocalDate date) {
        return transactionJpaRepository.findAllTransactionAmountSumBeforeDate(date);
    }

}

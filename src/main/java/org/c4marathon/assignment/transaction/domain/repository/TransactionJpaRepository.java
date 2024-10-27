package org.c4marathon.assignment.transaction.domain.repository;

import org.c4marathon.assignment.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Integer> {

    // index(transactionDate)
    @Query("""
    SELECT t
    FROM Transaction t
    ORDER BY t.transactionDate ASC, t.id ASC
    LIMIT :size
    """)
    List<Transaction> findTransaction(@Param("size") int size);


    @Query("""
    SELECT t
    FROM Transaction t
    WHERE (t.transactionDate > :transactionDate OR (t.transactionDate = :transactionDate AND t.id > :id))
    AND DATE(t.transactionDate) <= :endDate
    ORDER BY t.transactionDate ASC, t.id ASC
    LIMIT :size
    """)
    List<Transaction> findTransactionWithEndDateAndLastDate(
            @Param("endDate") LocalDate endDate,
            @Param("transactionDate") Instant transactionDate,
            @Param("id") int id,
            @Param("size") int size
    );

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE DATE(t.transactionDate) = :transactionDate
    ORDER BY t.transactionDate ASC, t.id ASC
    LIMIT :size
    """)
    List<Transaction> findTransactionByDate(@Param("transactionDate") LocalDate transactionDate, @Param("size") int size);


    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t
    """)
    Long findAllTransactionAmountSum();

    @Query("""
    SELECT SUM(t.amount)
    FROM Transaction t
    WHERE DATE(t.transactionDate) < :transactionDate
    """)
    Long findAllTransactionAmountSumBeforeDate(@Param("transactionDate") LocalDate transactionDate);
}

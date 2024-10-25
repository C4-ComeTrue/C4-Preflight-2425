package org.c4marathon.assignment.transaction.domain.repository;

import org.c4marathon.assignment.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Integer> {

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
    WHERE (t.transactionDate > :lastDate OR (t.transactionDate = :lastDate AND t.id > :lastId))
    AND DATE(t.transactionDate) <= :endDate
    ORDER BY t.transactionDate ASC, t.id ASC
    LIMIT :size
    """)
    List<Transaction> findTransactionWithEndDateAndLastDate(
            @Param("endDate") LocalDate endDate,
            @Param("lastDate") Instant lastDate,
            @Param("lastId") Integer lastId,
            @Param("size") int size
    );

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE DATE(t.transactionDate) = :date
    ORDER BY t.transactionDate ASC, t.id ASC
    LIMIT :size
    """)
    List<Transaction> findTransactionByDate(@Param("date") LocalDate date, @Param("size") int size);


}

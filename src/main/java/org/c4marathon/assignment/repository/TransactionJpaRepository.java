package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

    @Query("""
    SELECT MIN(t.transactionDate) FROM Transaction t
    """)
    Instant findEarliestTransactionDate();

    @Query("""

            SELECT t
    FROM Transaction t
    ORDER BY t.transactionDate , t.id
    LIMIT :size
    """)
    List<Transaction> findTransaction(@Param("size") int size);

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.transactionDate < :endDate
              AND (t.transactionDate > :lastDate)
              OR (t.transactionDate = :lastDate AND t.id > :lastDateId)
    ORDER BY t.transactionDate, t.id 
    LIMIT :size
    """)
    List<Transaction> findTransactionWithEndDateAndLastDate(@Param("endDate") Instant endDate, @Param("lastDate") Instant lastDate, @Param("lastDateId") int lastDateId, @Param("size") int size);

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE (t.transactionDate >= :startDate) AND (t.transactionDate < :endDate)
    ORDER BY t.transactionDate , t.id
    LIMIT :size
    """)
    List<Transaction> findTransactionWithEndDate(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("size") int size);

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE ((t.transactionDate >= :startDate) AND (t.transactionDate < :endDate))
              AND (t.transactionDate > :lastDate)
              OR (t.transactionDate = :lastDate AND t.id > :lastDateId)
    ORDER BY t.transactionDate, t.id 
    LIMIT :size
    """)
    List<Transaction> findOneDayTransactionWithEndDateAndLastDate(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate, @Param("lastDate") Instant lastDate, @Param("lastDateId") int lastDateId, @Param("size") int size);


}
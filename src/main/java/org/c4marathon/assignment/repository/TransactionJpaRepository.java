package org.c4marathon.assignment.repository;

import java.time.Instant;
import java.util.List;

import org.c4marathon.assignment.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0)
		FROM Transaction t WHERE :startDate <= t.transactionDate AND t.transactionDate < :endDate
		""")
	Long sumAmountBetweenDate(@Param("startDate") Instant startInstantDate, @Param("endDate") Instant endInstantDate);

	@Query("""
		SELECT COALESCE(SUM(t.amount), 0)
		FROM Transaction t WHERE t.id <= (
								  SELECT MAX(t.id)
								  FROM Transaction t WHERE t.transactionDate <= :date
					  			)
		""")
	Long cumulativeRemittanceBeforeDate(@Param("date") Instant date);

	@Query("""
		SELECT t
		FROM Transaction t WHERE t.senderAccount = :account 
		OR t.receiverAccount = :account
		""")
	List<Transaction> findAllTransaction(@Param("account") String account);
}

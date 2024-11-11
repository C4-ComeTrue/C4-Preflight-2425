package org.c4marathon.assignment.domain.transaction.repository;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	@Query(
		"""
			SELECT new TransferStatistics(SUM(t.amount), 
			SUM(SUM(t.amount)) OVER (ORDER BY DATE(t.transactionDate)), t.transactionDate) 
			FROM Transaction t
			WHERE t.transactionDate BETWEEN :startDate AND :endDate
			GROUP BY t.transactionDate
			ORDER BY t.transactionDate DESC
			""")
	Stream<TransferStatistics> findTransactionBetween(@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate);
}
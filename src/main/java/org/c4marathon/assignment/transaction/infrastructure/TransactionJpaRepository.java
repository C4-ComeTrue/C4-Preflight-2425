package org.c4marathon.assignment.transaction.infrastructure;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
	@Query(value = """
		SELECT *
		FROM transaction t
		ORDER BY t.transaction_date
		LIMIT 1
	""", nativeQuery = true)
	Optional<Transaction> findFirstOrderByTransactionDate();

	@Query(value = """
		SELECT *
		FROM transaction t
		WHERE transaction_date >= :start AND transaction_date < :end
		ORDER BY t.transaction_date, transaction_id
		LIMIT :limit
	""", nativeQuery = true)
	List<Transaction> findByTransactionDateBetween(Instant start, Instant end, int limit);

	@Query(value = """
		SELECT *
		FROM transaction t
		WHERE (transaction_date > :start OR (transaction_date = :start AND transaction_id > :id)) AND transaction_date < :end
		ORDER BY t.transaction_date, transaction_id
		LIMIT :limit
	""", nativeQuery = true)
	List<Transaction> findByTransactionDateBetweenWithCursor(Instant start, Instant end, long id, int limit);
}

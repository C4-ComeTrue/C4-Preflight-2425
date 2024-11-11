package org.c4marathon.assignment.domain.transfer_statistics.repository;

import static org.hibernate.jpa.HibernateHints.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;

public interface TransferStatisticsRepository extends JpaRepository<TransferStatistics, Long> {
	@Query(
		"""
			SELECT ts FROM TransferStatistics ts
			WHERE ts.unitDate between :startDate AND :endDate
			ORDER BY ts.unitDate DESC
			""")
	@QueryHints(value = {
		@QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE),
		@QueryHint(name = HINT_CACHEABLE, value = "false"),
	})
	Stream<TransferStatistics> findAllByUnitDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	Optional<TransferStatistics> findByUnitDate(LocalDate date);
}

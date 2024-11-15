package org.c4marathon.assignment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticJpaRepository extends JpaRepository<Statistic, Long> {
	List<Statistic> findAllByStatisticDateGreaterThanEqualAndStatisticDateBefore(Instant startDate, Instant endDate);

	Optional<Statistic> findByStatisticDateGreaterThanEqualAndStatisticDateBefore(Instant startDate, Instant endDate);
}

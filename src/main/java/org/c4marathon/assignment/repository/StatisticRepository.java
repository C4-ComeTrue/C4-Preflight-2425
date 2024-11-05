package org.c4marathon.assignment.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.entity.Statistic;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StatisticRepository {
	private final StatisticJpaRepository statisticJpaRepository;

	public List<Statistic> findAllByStatisticDateBetween(Instant startDate, Instant endDate) {
		return statisticJpaRepository.findAllByStatisticDateBetween(startDate, endDate);
	}

	public Optional<Statistic> findByStatisticDate(Instant startDate, Instant endDate) {
		return statisticJpaRepository.findByStatisticDate(startDate, endDate);
	}

	public Statistic save(Statistic statistic) {
		return statisticJpaRepository.save(statistic);
	}
}

package org.c4marathon.assignment.repository;

import java.time.Instant;
import java.util.List;

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
}

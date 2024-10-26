package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.CumulativeAmount;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CumulativeRepository {
	private final CumulativeJpaRepository cumulativeJpaRepository;

	public List<CumulativeAmount> findByDateBetween(LocalDate startDate, LocalDate endDate) {
		return cumulativeJpaRepository.findByDateBetween(startDate, endDate);
	}

	public CumulativeAmount findByDate(LocalDate date) {
		return cumulativeJpaRepository.findByDate(date);
	}

	public void save(CumulativeAmount cumulativeAmount) {
		cumulativeJpaRepository.save(cumulativeAmount);
	}
}

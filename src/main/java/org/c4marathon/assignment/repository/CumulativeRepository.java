package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;

import org.c4marathon.assignment.domain.CumulativeAmount;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CumulativeRepository {
	private final CumulativeJpaRepository cumulativeJpaRepository;

	public List<CumulativeAmount> findByDateBetween(LocalDate startDate, LocalDate endDate) {
		return cumulativeJpaRepository.findByDateBetween(startDate, endDate);
	}

	public Optional<CumulativeAmount> findByDate(LocalDate date) {
		return cumulativeJpaRepository.findByDate(date);
	}

	public void save(CumulativeAmount cumulativeAmount) {
		cumulativeJpaRepository.save(cumulativeAmount);
	}

	public void saveAll(List<CumulativeAmount> cumulativeAmountList){
		cumulativeJpaRepository.saveAll(cumulativeAmountList);
	}
}

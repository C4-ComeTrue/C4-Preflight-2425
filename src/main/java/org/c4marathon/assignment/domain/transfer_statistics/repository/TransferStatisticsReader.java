package org.c4marathon.assignment.domain.transfer_statistics.repository;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferStatisticsReader {
	private final TransferStatisticsRepository transferStatisticsRepository;

	public Stream<TransferStatistics> findAllByUnitDateBetween(LocalDate from, LocalDate to) {
		return transferStatisticsRepository.findAllByUnitDateBetween(from, to);
	}

	public TransferStatistics findByUnitDate(LocalDate date) {
		return transferStatisticsRepository.findByUnitDate(date)
			.orElseThrow(() -> new EntityNotFoundException("해당 송금 통계는 존재하지 않습니다."));
	}
}

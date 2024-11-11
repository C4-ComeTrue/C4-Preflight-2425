package org.c4marathon.assignment.domain.transfer_statistics.repository;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferStatisticsReader {
	private final TransferStatisticsRepository transferStatisticsRepository;

	public Stream<TransferStatistics> findAllByUnitDateBetween(LocalDate from, LocalDate to) {
		return transferStatisticsRepository.findAllByUnitDateBetween(from, to);
	}
}

package org.c4marathon.assignment.domain.transfer_statistics.repository;

import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransferStatisticsStore {
	private final TransferStatisticsRepository transferStatisticsRepository;

	public TransferStatistics store(TransferStatistics transferStatistics) {
		return transferStatisticsRepository.save(transferStatistics);
	}
}

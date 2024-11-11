package org.c4marathon.assignment.domain.transfer_statistics.service;

import java.time.LocalDateTime;
import java.util.List;

import org.c4marathon.assignment.domain.transaction.repository.TransactionReader;
import org.c4marathon.assignment.domain.transfer_statistics.dto.GetAllTransferStatisticsRequest;
import org.c4marathon.assignment.domain.transfer_statistics.dto.AggregateTransferStatisticsRequest;
import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.c4marathon.assignment.domain.transfer_statistics.repository.TransferStatisticsReader;
import org.c4marathon.assignment.domain.transfer_statistics.repository.TransferStatisticsStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransferStatisticsService {
	private final TransferStatisticsReader transferStatisticsReader;
	private final TransferStatisticsStore transferStatisticsStore;
	private final TransactionReader transactionReader;

	@Transactional(readOnly = true)
	public List<TransferStatistics> findAllByUnitDateBetween(GetAllTransferStatisticsRequest request) {
		return transferStatisticsReader.findAllByUnitDateBetween(request.startDate(), request.endDate())
			.toList();
	}

	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	public void getScheduledStatistics() {
		var yesterday = LocalDateTime.now().minusDays(1).toLocalDate();
		var allTransfersByDate = transactionReader.findAllTransfersByDate(yesterday);
		var transferStatistics = transferStatisticsReader.findByUnitDate(yesterday);
		transferStatisticsStore.store(new TransferStatistics(allTransfersByDate, transferStatistics));
	}

	@Transactional
	public TransferStatistics getStatistics(AggregateTransferStatisticsRequest request) {
		var targetDate = request.targetDate().toLocalDate();

		var transferStatistics = transferStatisticsReader.findByUnitDate(targetDate);

		if (transferStatistics != null) {
			return transferStatistics;
		}

		var allTransfersByDate = transactionReader.findAllTransfersByDate(targetDate);
		transferStatistics = transferStatisticsReader.findByUnitDate(targetDate.minusDays(1));
		return transferStatisticsStore.store(new TransferStatistics(allTransfersByDate, transferStatistics));
	}
}

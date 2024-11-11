package org.c4marathon.assignment.domain.transfer_statistics.service;

import java.time.LocalDateTime;
import java.util.List;

import org.c4marathon.assignment.domain.transaction.repository.TransactionReader;
import org.c4marathon.assignment.domain.transfer_statistics.dto.AggregateTransferStatisticsRequest;
import org.c4marathon.assignment.domain.transfer_statistics.dto.GetAllTransferStatisticsRequest;
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
		var today = LocalDateTime.now();
		var yesterday = today.minusDays(1);
		calculate(yesterday, today);
	}

	private TransferStatistics calculate(LocalDateTime yesterday, LocalDateTime today) {
		var allTransfersByDate = transactionReader.findTransferStatisticsBetweeen(yesterday.toLocalDate(),
			today.toLocalDate());
		var transferStatistics = transferStatisticsReader.findByUnitDate(yesterday.toLocalDate());
		Long dailyTotal = allTransfersByDate.map(TransferStatistics::getDailyTotalAmount).reduce(0L, Long::sum);
		Long cumulativeTotal = transferStatistics.getCumulativeTotalAmount() + allTransfersByDate.map(
			TransferStatistics::getCumulativeTotalAmount).reduce(0L, Long::sum);
		return transferStatisticsStore.store(new TransferStatistics(dailyTotal, cumulativeTotal, today));
	}

	@Transactional
	public TransferStatistics getStatistics(AggregateTransferStatisticsRequest request) {
		var targetDate = request.targetDate();
		var previousDate = targetDate.minusDays(1);

		var transferStatistics = transferStatisticsReader.findByUnitDate(targetDate.toLocalDate());

		if (transferStatistics != null) {
			return transferStatistics;
		}

		return calculate(previousDate, targetDate);
	}
}

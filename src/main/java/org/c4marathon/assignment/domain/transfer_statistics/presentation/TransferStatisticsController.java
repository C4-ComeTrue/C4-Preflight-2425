package org.c4marathon.assignment.domain.transfer_statistics.presentation;

import java.time.LocalDate;
import java.util.List;

import org.c4marathon.assignment.domain.transfer_statistics.dto.AggregateTransferStatisticsRequest;
import org.c4marathon.assignment.domain.transfer_statistics.dto.GetAllTransferStatisticsRequest;
import org.c4marathon.assignment.domain.transfer_statistics.entity.TransferStatistics;
import org.c4marathon.assignment.domain.transfer_statistics.service.TransferStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransferStatisticsController {
	private final TransferStatisticsService transferStatisticsService;

	@GetMapping("/v1/transfer-statistics")
	public ResponseEntity<List<TransferStatistics>> getTransferStatisticsInBetween(
		@RequestParam(name = "start-date") LocalDate startDate, @RequestParam(name = "end-date") LocalDate endDate) {
		return ResponseEntity.ok(
			transferStatisticsService.findAllByUnitDateBetween(
				new GetAllTransferStatisticsRequest(startDate, endDate)));
	}

	@PostMapping("/v1/transfer-statistics")
	public ResponseEntity<TransferStatistics> aggregateTransferStatistics(
		@RequestBody AggregateTransferStatisticsRequest request
	) {
		return ResponseEntity.ok(transferStatisticsService.getStatistics(request));
	}
}

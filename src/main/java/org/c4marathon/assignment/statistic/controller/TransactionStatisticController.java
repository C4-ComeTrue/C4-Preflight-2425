package org.c4marathon.assignment.statistic.controller;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.c4marathon.assignment.statistic.application.TransactionStatisticService;
import org.c4marathon.assignment.statistic.dto.TransactionStatisticResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistic/transaction")
public class TransactionStatisticController {
	private final TransactionStatisticService statisticService;

	public TransactionStatisticController(TransactionStatisticService statisticService) {
		this.statisticService = statisticService;
	}

	@GetMapping("/aggregate")
	public ResponseEntity<TransactionStatisticResult> aggregate(@RequestParam LocalDate theDay) {
		return ResponseEntity.ok(statisticService.aggregate(theDay.atStartOfDay(ZoneOffset.UTC).toInstant()));
	}

	@GetMapping("/range")
	public ResponseEntity<List<TransactionStatisticResult>> findAll(@RequestParam LocalDate startTime, @RequestParam LocalDate endTime) {
		return ResponseEntity.ok(statisticService.findAll(startTime.atStartOfDay(ZoneOffset.UTC).toInstant(),
			endTime.atStartOfDay(ZoneOffset.UTC).toInstant()));
	}
}

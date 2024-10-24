package org.c4marathon.assignment.statistic.controller;

import org.c4marathon.assignment.statistic.application.TransactionStatisticService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionStatisticController {
	private final TransactionStatisticService statisticService;

	public TransactionStatisticController(TransactionStatisticService statisticService) {
		this.statisticService = statisticService;
	}
}

package org.c4marathon.assignment.controller;

import org.c4marathon.assignment.dto.response.FinancialInfoRes;
import org.c4marathon.assignment.service.MarathonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/marathon")
@RequiredArgsConstructor
public class MarathonController {
	private final MarathonService marathonService;

	@GetMapping("/all-info")
	public FinancialInfoRes getAllInfoWithLimit(@RequestParam String email) {
		return marathonService.getAllInfoWithLimit(email);
	}

	@GetMapping("/all-info-not-parallel")
	public FinancialInfoRes getAllInfoNotParallel(@RequestParam String email) {
		return marathonService.getAllInfoNotParallel(email);
	}
}

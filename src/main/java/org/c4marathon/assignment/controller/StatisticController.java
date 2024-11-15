package org.c4marathon.assignment.controller;

import java.time.LocalDate;
import java.util.List;

import org.c4marathon.assignment.dto.response.StatisticRes;
import org.c4marathon.assignment.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
	private final StatisticService statisticService;

	/**
	 시작 날짜와 종료 날짜를 받아 테이블 조회하는 API
	 */
	@GetMapping("/range")
	public ResponseEntity<List<StatisticRes>> getStatisticByDate(
		@RequestParam LocalDate startDate,
		@RequestParam LocalDate endDate
	) {
		return ResponseEntity.ok(statisticService.getStatisticsByDateRange(startDate, endDate));
	}

	/**
	 특정 날짜의 통계를 강제로 집계
	 */
	@GetMapping("/recalculate")
	public ResponseEntity<StatisticRes> recalculateStatistic(@RequestParam LocalDate date) {
		return ResponseEntity.ok(statisticService.recalculateStatistic(date));
	}
}

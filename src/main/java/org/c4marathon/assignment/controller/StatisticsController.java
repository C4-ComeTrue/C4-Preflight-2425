package org.c4marathon.assignment.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.controller.response.CumulativeAmountResponse;
import org.c4marathon.assignment.service.CumulativeAmountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/statics")
@RequiredArgsConstructor
public class StatisticsController {

    private final CumulativeAmountService cumulativeAmountService;

    /**
     * 1. 특정 날짜의 통계를 강제로 집계 하는 API
     *
     */
    @GetMapping("/cumulative")
    public ResponseEntity<CumulativeAmountResponse> getCumulativeAmountDate(@RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ResponseEntity.ok(cumulativeAmountService.getCumulativeAmountDate(date));
    }

    /**
     * 2. 주어진 날짜 사이의 통계값을 조회 하는 API
     */
    @GetMapping("/cumulative/range")
    public ResponseEntity<List<CumulativeAmountResponse>> getCumulativeAmountRangeDate(@RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @NotBlank @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseEntity.ok(cumulativeAmountService.getCumulativeAmountRangeDate(startDate, endDate));
    }
}

package org.c4marathon.assignment.statistics.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.statistics.dto.StatisticsResponse;
import org.c4marathon.assignment.statistics.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;

    @PostMapping("/statistics")
    public ResponseEntity<Void> getStatistics(@RequestParam int pageSize, @RequestParam @NotNull LocalDate date) {
        statisticsService.calculateStatistics(pageSize, date);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<StatisticsResponse>> getStatisticsByStartDateAndEndDate(
            @RequestParam("startDate") @NotNull LocalDate startDate,
            @RequestParam("endDate") @NotNull LocalDate endDate
    ) {
        List<StatisticsResponse> statisticsByDate = statisticsService.getStatisticsByStartDateAndEndDate(startDate, endDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(statisticsByDate);
    }
}

package org.c4marathon.assignment.statistics.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.statistics.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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
}

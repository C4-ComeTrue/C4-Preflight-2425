package org.c4marathon.assignment.statistics.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "statistics_opixxx")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistics_id")
    private Long id;

    @Column(nullable = false)
    private LocalDate statisticsDate; //통계 기준 날짜

    @Column(nullable = false)
    private Long totalRemittance; //당일 송금

    @Column(nullable = false)
    private Long cumulativeRemittance; //누적 송금 금액

    @Column(nullable = false)
    private LocalDateTime createAt; //통계를 낸 시간

    @Builder
    private Statistics(LocalDate statisticsDate, Long totalRemittance, Long cumulativeRemittance, LocalDateTime createAt) {
        this.statisticsDate = statisticsDate;
        this.totalRemittance = totalRemittance;
        this.cumulativeRemittance = cumulativeRemittance;
        this.createAt = createAt;
    }

    public static Statistics of(LocalDate statisticsDate, Long totalRemittance, Long cumulativeRemittance, LocalDateTime createAt) {
        return Statistics.builder()
                .statisticsDate(statisticsDate)
                .totalRemittance(totalRemittance)
                .cumulativeRemittance(cumulativeRemittance)
                .createAt(createAt)
                .build();
    }

    public void update(LocalDate statisticsDate, Long totalRemittance, Long cumulativeRemittance, LocalDateTime createAt) {
        this.statisticsDate = statisticsDate;
        this.totalRemittance += totalRemittance;
        this.cumulativeRemittance = cumulativeRemittance;
        this.createAt = createAt;
    }


}

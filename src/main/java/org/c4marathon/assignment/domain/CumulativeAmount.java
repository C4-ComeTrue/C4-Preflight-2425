package org.c4marathon.assignment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "statistics_cumulative_amount_hellozo0")
public class CumulativeAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull
    @Column(name = "daily_amount", nullable = false)
    private Long dailyAmount;

    @NotNull
    @Column(name = "cumulative_amount", nullable = false)
    private Long cumulativeAmount;

    public CumulativeAmount(LocalDate date, Long dailyAmount, Long cumulativeAmount) {
        this.date = date;
        this.dailyAmount = dailyAmount;
        this.cumulativeAmount = cumulativeAmount;
    }

    public void updateDailyAmount(Long dailyAmount) {
        this.dailyAmount = dailyAmount;
    }

    public void updateCumulativeAmount(Long cumulativeAmount) {
        this.cumulativeAmount = cumulativeAmount;
    }

}

package org.c4marathon.assignment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Entity
@Getter
@Table(name = "statistics_cumulative_amount_hellozo0")
public class CumulativeAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "daily_amount", nullable = false)
    private Integer dailyAmount;

    @NotNull
    @Column(name = "cumulative_amount", nullable = false)
    private Long cumulativeAmount;
}

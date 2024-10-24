package org.c4marathon.assignment.statistic.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "transaction_statistic_zzamba")
public class TransactionStatistic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "datetime(6)")
	private Instant statisticDate;

	@Column(nullable = false)
	private long dailyTotalAmount;

	@Column(nullable = false)
	private long cumulativeAmount;
}

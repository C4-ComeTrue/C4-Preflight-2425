package org.c4marathon.assignment.statistic.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "transaction_statistic_zzamba")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Builder
	public TransactionStatistic(Instant statisticDate, long dailyTotalAmount, long cumulativeAmount) {
		this.statisticDate = statisticDate;
		this.dailyTotalAmount = dailyTotalAmount;
		this.cumulativeAmount = cumulativeAmount;
	}
}

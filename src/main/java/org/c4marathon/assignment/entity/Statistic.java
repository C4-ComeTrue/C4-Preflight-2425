package org.c4marathon.assignment.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "statistic_nyh365", indexes = @Index(name = "idx_statistic_date", columnList = "statisticDate"))
public class Statistic extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@NotNull
	@Column(nullable = false)
	private Long totalRemittance;

	@NotNull
	@Column(nullable = false)
	private Long cumulativeRemittance;

	@NotNull
	@Column(nullable = false)
	private Instant statisticDate;

	public Statistic(Instant statisticDate) {
		this.statisticDate = statisticDate;
	}

	@Builder
	public Statistic(Long totalRemittance, Long cumulativeRemittance, Instant statisticDate) {
		this.totalRemittance = totalRemittance;
		this.cumulativeRemittance = cumulativeRemittance;
		this.statisticDate = statisticDate;
	}
}

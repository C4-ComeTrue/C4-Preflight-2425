package org.c4marathon.assignment.domain.transfer_statistics.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.c4marathon.assignment.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfer_statistics_hodadako")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferStatistics extends BaseEntity {
	@Column(name = "daily_total_amount", columnDefinition = "bigint")
	private Long dailyTotalAmount;

	@Column(name = "cumulative_total_amount", columnDefinition = "bigint")
	private Long cumulativeTotalAmount;

	@Column(name = "unit_date", columnDefinition = "datetime")
	private LocalDateTime unitDate;

	public TransferStatistics(Long dailyTotalAmount, Long cumulativeTotalAmount, LocalDateTime unitDate) {
		this.dailyTotalAmount = dailyTotalAmount;
		this.cumulativeTotalAmount = cumulativeTotalAmount;
		this.unitDate = unitDate;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TransferStatistics transferStatistics) {
			return this.getId() == transferStatistics.getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this);
	}
}

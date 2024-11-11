package org.c4marathon.assignment.domain.transfer_statistics.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import org.c4marathon.assignment.common.entity.BaseEntity;
import org.c4marathon.assignment.domain.transaction.entity.Transaction;

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
	@Column(name = "daily_total_amount", columnDefinition = "decimal(15, 2)")
	private BigDecimal dailyTotalAmount;

	@Column(name = "cumulative_total_amount", columnDefinition = "decimal(15, 2)")
	private BigDecimal cumulativeTotalAmount;

	@Column(name = "unit_date", columnDefinition = "timestamp")
	private LocalDateTime unitDate;

	public TransferStatistics(Stream<Transaction> transactions, TransferStatistics transferStatistics) {
		BigDecimal currentDailyTotal = transactions.map(Transaction::getAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal previousCumulativeTotal = transferStatistics.getCumulativeTotalAmount();
		this.dailyTotalAmount = currentDailyTotal;
		this.cumulativeTotalAmount = previousCumulativeTotal.add(currentDailyTotal);
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

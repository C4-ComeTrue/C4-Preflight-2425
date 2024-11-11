package org.c4marathon.assignment.domain.transaction.entity;

import java.math.BigDecimal;
import java.util.Objects;

import org.c4marathon.assignment.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_hodadako")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
	@Enumerated(EnumType.STRING)
	@Column(name = "transaction_type", columnDefinition = "varchar(20)")
	private TransactionType transactionType;

	@Column(name = "amount", columnDefinition = "decimal(15, 2)")
	private BigDecimal amount;

	@Override
	public boolean equals(Object o) {
		if (o instanceof Transaction t) {
			return this.getId() == t.getId();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this);
	}
}

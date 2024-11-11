package org.c4marathon.assignment.domain.transaction.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.c4marathon.assignment.common.entity.BaseEntity;
import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction", indexes = {})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id") // 여기서 컬럼명을 "transaction_id"로 변경
	private Integer id;

	@Column(name = "sender_account", columnDefinition = "varchar(20)", nullable = false)
	private String senderAccount;

	@Column(name = "receiver_account", columnDefinition = "varchar(20)", nullable = false)
	private String receiverAccount;

	@Column(name = "sender_swift_code", columnDefinition = "varchar(11)", nullable = false)
	private String senderSwiftCode;

	@Column(name = "receiver_swift_code", columnDefinition = "varchar(11)", nullable = false)
	private String receiverSwiftCode;

	@Column(name = "sender_name", columnDefinition = "varchar(30)", nullable = false)
	private String senderName;

	@Column(name = "receiver_name", columnDefinition = "varchar(30)", nullable = false)
	private String receiverName;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "memo", columnDefinition = "varchar(200)")
	private String memo;

	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "transaction_date", nullable = false)
	private LocalDateTime transactionDate;

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

package org.c4marathon.assignment.transaction.domain;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "transaction")
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "transaction_id", nullable = false)
	private Integer id;

	@Column(name = "sender_account", nullable = false, length = 20)
	private String senderAccount;

	@Column(name = "receiver_account", nullable = false, length = 20)
	private String receiverAccount;

	@Column(name = "sender_swift_code", nullable = false, length = 11)
	private String senderSwiftCode;

	@Column(name = "receiver_swift_code", nullable = false, length = 11)
	private String receiverSwiftCode;

	@Column(name = "sender_name", nullable = false, length = 30)
	private String senderName;

	@Column(name = "receiver_name", nullable = false, length = 30)
	private String receiverName;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "memo", length = 200)
	private String memo;

	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "transaction_date", nullable = false)
	private Instant transactionDate;

}

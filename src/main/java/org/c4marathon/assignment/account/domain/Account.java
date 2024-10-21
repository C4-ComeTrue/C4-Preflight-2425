package org.c4marathon.assignment.account.domain;

import java.time.Instant;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "account")
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id", nullable = false)
	private Integer id;

	@Column(name = "account_number", nullable = false, length = 17)
	private String accountNumber;

	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(name = "account_type", nullable = false)
	private char accountType;

	@Column(name = "memo", length = 200)
	private String memo;

	@ColumnDefault("0")
	@Column(name = "balance", nullable = false)
	private Long balance;

	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "create_date", nullable = false)
	private Instant createDate;

	@Column(name = "recent_transaction_date")
	private Instant recentTransactionDate;

}

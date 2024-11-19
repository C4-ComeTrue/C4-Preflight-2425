package org.c4marathon.assignment.domain.account.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;

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
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Integer id;

	@Column(name = "account_number", columnDefinition = "varchar(17)")
	private String accountNumber;

	@Column(name = "user_id", columnDefinition = "int")
	private Integer userId;

	@Column(name = "account_type", columnDefinition = "char")
	private Character accountType;

	@Column(name = "memo", columnDefinition = "varchar(200)")
	private String memo;

	@ColumnDefault("0")
	@Column(name = "balance", columnDefinition = "bigint")
	private Long balance;

	@CreatedDate
	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "create_date", columnDefinition = "timestamp")
	private LocalDateTime createDate;

	@Column(name = "recent_transaction_date", columnDefinition = "timestamp")
	private LocalDateTime recentTransactionDate;
}
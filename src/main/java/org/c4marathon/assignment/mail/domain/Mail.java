package org.c4marathon.assignment.mail.domain;

import java.time.LocalDateTime;

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
@Table(name = "mail_zzamba")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "datetime(6)")
	private LocalDateTime requestTime;

	@Column(columnDefinition = "datetime(6)")
	private LocalDateTime sendTime;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long accountId;

	@Builder
	public Mail(LocalDateTime requestTime, LocalDateTime sendTime, Long userId, Long accountId) {
		this.requestTime = requestTime;
		this.sendTime = sendTime;
		this.userId = userId;
		this.accountId = accountId;
	}
}

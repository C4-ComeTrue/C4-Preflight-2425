package org.c4marathon.assignment.user.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", nullable = false)
	private Integer id;

	@Column(name = "username", nullable = false, length = 30)
	private String username;

	@Column(name = "email", nullable = false, length = 30)
	private String email;

	@Column(name = "nickname", nullable = false, length = 10)
	private String nickname;

	@Column(name = "group_id", nullable = false)
	private int groupId;

	@Column(name = "user_status", nullable = false)
	private char userStatus;

	@Column(name = "create_date", nullable = false)
	private Instant createDate;

	@Column(name = "update_date", nullable = false)
	private Instant updateDate;

}

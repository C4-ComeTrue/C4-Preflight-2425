package org.c4marathon.assignment.domain.member.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", columnDefinition = "int")
	private Long id;

	@Column(name = "email", columnDefinition = "varchar(30)", unique = true)
	private String email;

	@Column(name = "username", columnDefinition = "varchar(30)")
	private String name;

	@Column(name = "nickname", columnDefinition = "varchar(10)")
	private String nickname;

	@Column(name = "group_id", columnDefinition = "int")
	private Integer groupId;

	@Column(name = "user_status", columnDefinition = "char")
	private String userStatus;

	@CreatedDate
	@Column(name = "create_date", columnDefinition = "timestamp")
	private LocalDateTime createDate;

	@LastModifiedDate
	@Column(name = "update_date", columnDefinition = "timestamp")
	private LocalDateTime updateDate;
}

package org.c4marathon.assignment.domain.member.entity;

import org.c4marathon.assignment.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_hodadako")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Column(name = "email", columnDefinition = "varchar(30)", unique = true)
	private String email;

	@Column(name = "name", columnDefinition = "varchar(20)")
	private String name;
}

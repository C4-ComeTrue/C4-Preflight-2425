package org.c4marathon.assignment.domain.email_record.entity;

import org.c4marathon.assignment.common.entity.BaseEntity;
import org.c4marathon.assignment.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_record_hodadako")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailRecord extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", columnDefinition = "bigint", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@Enumerated
	@Column(name = "email_status", columnDefinition = "varchar(10)")
	private EmailStatus emailStatus;

	@Column(name = "subject", columnDefinition = "varchar(50)")
	private String subject;

	@Column(name = "content", columnDefinition = "text")
	private String content;

	public EmailRecord(Member member, String content, String subject) {
		this.emailStatus = EmailStatus.PENDING;
		this.member = member;
		this.content = content;
		this.subject = subject;
	}

	public void setStatusToCompleted() {
		this.emailStatus = EmailStatus.COMPLETED;
	}

	public void setStatusToFailed() {
		this.emailStatus = EmailStatus.FAILED;
	}
}

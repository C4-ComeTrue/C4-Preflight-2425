package org.c4marathon.assignment.domain.email_record.dto;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SaveEmailRecordRequest(
	@NotNull
	@Positive
	Long memberId,
	@NotBlank
	String content,
	@NotBlank
	@Size(max = 50)
	String subject
) {
	public EmailRecord toEntity(Member member) {
		return new EmailRecord(member, this.content, this.subject);
	}
}

package org.c4marathon.assignment.domain.email_record.dto;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.member.entity.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmailRecordRequest {
	public record Save(
		@NotNull
		Long memberId,
		@NotBlank
		String content
	) {
		public EmailRecord toEntity(Member member) {
			return new EmailRecord(member, this.content);
		}
	}
}

package org.c4marathon.assignment.domain.email_record.event;

import java.util.UUID;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailRecordEvent {
	private final UUID eventId;
	private final Long emailRecordId;
	private final String email;
	private final String subject;
	private final String content;

	public static EmailRecordEvent toEvent(EmailRecord emailRecord) {
		return new EmailRecordEvent(UUID.randomUUID(), emailRecord.getId(), emailRecord.getMember().getEmail(),
			emailRecord.getSubject(), emailRecord.getContent());
	}
}

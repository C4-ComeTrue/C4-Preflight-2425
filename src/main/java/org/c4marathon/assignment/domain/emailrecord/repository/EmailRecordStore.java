package org.c4marathon.assignment.domain.emailrecord.repository;

import org.c4marathon.assignment.domain.emailrecord.entity.EmailRecord;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailRecordStore {
	private final EmailRecordRepository emailRecordRepository;

	public EmailRecord store(EmailRecord emailRecord) {
		return emailRecordRepository.save(emailRecord);
	}
}

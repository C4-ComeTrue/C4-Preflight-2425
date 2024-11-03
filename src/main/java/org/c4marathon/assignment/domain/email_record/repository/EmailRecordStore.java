package org.c4marathon.assignment.domain.email_record.repository;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailRecordStore {
	private final EmailRecordRepository emailRecordRepository;

	public void store(EmailRecord emailRecord) {
		emailRecordRepository.save(emailRecord);
	}
}

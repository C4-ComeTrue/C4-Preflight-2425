package org.c4marathon.assignment.domain.email_record.repository;

import java.util.List;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.email_record.entity.EmailStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailRecordReader {
	private final EmailRecordRepository emailRecordRepository;

	public List<EmailRecord> findAllPending() {
		return emailRecordRepository.findAllByEmailStatus(EmailStatus.PENDING);
	}
}

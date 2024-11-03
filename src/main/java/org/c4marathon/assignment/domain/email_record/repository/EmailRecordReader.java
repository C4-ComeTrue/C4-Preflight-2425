package org.c4marathon.assignment.domain.email_record.repository;

import java.util.List;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.email_record.entity.EmailStatus;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailRecordReader {
	private final EmailRecordRepository emailRecordRepository;

	public List<EmailRecord> findAllPending() {
		return emailRecordRepository.findAllByEmailStatus(EmailStatus.PENDING);
	}

	public EmailRecord find(Long id) {
		return emailRecordRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("해당 이메일 레코드는 존재하지 않습니다."));
	}
}

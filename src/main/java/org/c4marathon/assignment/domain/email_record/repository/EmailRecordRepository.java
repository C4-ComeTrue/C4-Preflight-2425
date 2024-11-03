package org.c4marathon.assignment.domain.email_record.repository;

import java.util.List;

import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.email_record.entity.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRecordRepository extends JpaRepository<EmailRecord, String> {
	List<EmailRecord> findAllByEmailStatus(EmailStatus emailStatus);
}

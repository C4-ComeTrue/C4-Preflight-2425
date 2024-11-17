package org.c4marathon.assignment.domain.emailrecord.repository;

import static org.hibernate.jpa.HibernateHints.*;

import java.util.stream.Stream;

import org.c4marathon.assignment.domain.emailrecord.entity.EmailRecord;
import org.c4marathon.assignment.domain.emailrecord.entity.EmailStatus;
import org.c4marathon.assignment.domain.emailrecord.query.EmailRecordQueryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;

public interface EmailRecordRepository extends JpaRepository<EmailRecord, Long> {
	@Query(
		"""
			SELECT new org.c4marathon.assignment.domain.emailrecord.query.EmailRecordQueryResult(er.id, er.member.email, er.subject, er.content) 
			FROM EmailRecord er 
			LEFT JOIN FETCH er.member
			WHERE er.emailStatus =:status
			ORDER BY er.id ASC
			""")
	@QueryHints(value = {
		@QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE),
		@QueryHint(name = HINT_CACHEABLE, value = "false"),
	})
	Stream<EmailRecordQueryResult> findAllByEmailStatus(@Param("status") EmailStatus emailStatus);
}
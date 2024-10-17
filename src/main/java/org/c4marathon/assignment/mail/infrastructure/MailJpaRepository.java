package org.c4marathon.assignment.mail.infrastructure;

import java.util.List;

import org.c4marathon.assignment.mail.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MailJpaRepository extends JpaRepository<Mail, Long> {
	@Query(value = "SELECT m.* FROM mail_zzamba m WHERE m.sendTime IS NULL LIMIT :limitSize", nativeQuery = true)
	List<Mail> findAllBySendTimeIsNullLimit(int limitSize);
}

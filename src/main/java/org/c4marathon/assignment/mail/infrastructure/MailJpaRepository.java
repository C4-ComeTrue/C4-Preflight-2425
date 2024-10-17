package org.c4marathon.assignment.mail.infrastructure;

import org.c4marathon.assignment.mail.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailJpaRepository extends JpaRepository<Mail, Long> {
}

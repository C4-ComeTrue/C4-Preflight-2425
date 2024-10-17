package org.c4marathon.assignment.mail.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MailRepository {
	private final CrudRepository<Mail, Long> mailCrudRepository;

	protected MailRepository(CrudRepository<Mail, Long> mailCrudRepository) {
		this.mailCrudRepository = mailCrudRepository;
	}

	public Mail saveMail(Mail mail) {
		return mailCrudRepository.save(mail);
	}
}

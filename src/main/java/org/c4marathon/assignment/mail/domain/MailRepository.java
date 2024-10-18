package org.c4marathon.assignment.mail.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.c4marathon.assignment.mail.dto.MailInfoToSendDto;
import org.c4marathon.assignment.mail.infrastructure.MailJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MailRepository {
	private final MailJpaRepository mailJpaRepository; // JPA와 연결 끊는 것도 고민

	protected MailRepository(MailJpaRepository mailJpaRepository) {
		this.mailJpaRepository = mailJpaRepository;
	}

	public Mail saveMail(Mail mail) {
		return mailJpaRepository.save(mail);
	}

	public List<MailInfoToSendDto> findAllToSend(int size) {
		return mailJpaRepository.findAllToSendLimit(size);
	}

	public void updateSendTime(List<Long> ids) {
		mailJpaRepository.updateSendTime(LocalDateTime.now(), ids);
	}
}

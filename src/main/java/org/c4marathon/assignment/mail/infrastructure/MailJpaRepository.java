package org.c4marathon.assignment.mail.infrastructure;

import java.time.LocalDateTime;
import java.util.List;

import org.c4marathon.assignment.mail.domain.Mail;
import org.c4marathon.assignment.mail.dto.MailInfoToSendDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MailJpaRepository extends JpaRepository<Mail, Long> {
	@Query(value = """
		SELECT m.id mailId, a.account_number accountNumber, u.email userEmail, u.nickname userNickname
		FROM mail_zzamba m
		JOIN user u ON m.user_id = u.user_id
		JOIN account a ON a.account_id = m.account_id
		LIMIT :limitSize
		""", nativeQuery = true)
	List<MailInfoToSendDto> findAllToSendLimit(int limitSize);

	@Modifying
	@Query(value = """
				UPDATE Mail m
				SET m.sendTime = :time
				WHERE m.id IN :ids
		""")
	void updateSendTime(LocalDateTime time, List<Long> ids);
}

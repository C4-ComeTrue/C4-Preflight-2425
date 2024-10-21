package org.c4marathon.assignment.mail.domain.repository;

import org.c4marathon.assignment.mail.domain.MailArchive;
import org.c4marathon.assignment.mail.domain.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MailRepository extends JpaRepository<MailArchive, Long> {

    //status에 index를 걸어서 조회하면 될듯?
    @Query("""
    SELECT m
    FROM MailArchive m
    WHERE m.status IN :statuses
    ORDER BY m.id ASC
    LIMIT 100
    """)
    List<MailArchive> findMailArchiveByStatusIn(@Param("statuses") List<MailStatus> statuses);
}

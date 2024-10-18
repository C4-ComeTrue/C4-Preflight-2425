package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailLogRepository extends JpaRepository<MailLog,Integer> {

    @Query("""
    SELECT m
    FROM MailLog m
    WHERE m.status IN :statuses
    """)
    List<MailLog> findMailLogsByStatusIn(@Param("statuses") List<MailStatus> statuses);
}

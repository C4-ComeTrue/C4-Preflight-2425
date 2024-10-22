package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailLogJpaRepository extends JpaRepository<MailLog, Integer> {

    List<MailLog> findMailLogsByStatusIn(List<MailStatus> statuses);
}
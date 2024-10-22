package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.domain.MailLog;
import org.c4marathon.assignment.domain.model.MailStatus;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MailLogRepository {
    private final MailLogJpaRepository mailLogJpaRepository;

    public List<MailLog> findMailLogsByStatusIn(List<MailStatus> statuses){
        return mailLogJpaRepository.findMailLogsByStatusIn(statuses);
    }

    public void save(MailLog mailLog){
        mailLogJpaRepository.save(mailLog);
    }

}

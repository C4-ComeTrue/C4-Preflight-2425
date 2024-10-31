package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.entity.EmailBox;
import org.c4marathon.assignment.entity.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmailBoxJpaRepository extends JpaRepository<EmailBox, Long> {
    List<EmailBox> findEmailBoxesByStatus(EmailStatus status);
}

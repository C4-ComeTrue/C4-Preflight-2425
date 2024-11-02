package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.entity.EmailBox;
import org.c4marathon.assignment.entity.EmailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmailBoxJpaRepository extends JpaRepository<EmailBox, Long> {

    List<EmailBox> findEmailBoxesByStatus(EmailStatus status);

    @Transactional
    @Modifying
    @Query(value = """
			UPDATE EmailBox e
			SET e.status = :status
			WHERE e.id IN :ids
		""")
    void updateStatus(EmailStatus status, List<Long> ids);
}

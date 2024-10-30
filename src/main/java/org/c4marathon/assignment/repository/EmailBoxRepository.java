package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.entity.EmailBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailBoxRepository extends JpaRepository<EmailBox, Long> {
}

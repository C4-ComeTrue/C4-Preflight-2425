package org.c4marathon.assignment.user.infrastructure;

import org.c4marathon.assignment.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
}

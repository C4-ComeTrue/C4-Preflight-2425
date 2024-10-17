package org.c4marathon.assignment.account.infrastructure;

import org.c4marathon.assignment.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountJpaRepository extends JpaRepository<Account, Integer> {
}

package org.c4marathon.assignment.domain.account.repository;

import java.util.List;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	List<Account> findByUserId(Integer userId);
}

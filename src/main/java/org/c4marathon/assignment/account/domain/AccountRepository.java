package org.c4marathon.assignment.account.domain;

import java.util.Optional;

import org.c4marathon.assignment.account.infrastructure.AccountJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
	private final AccountJpaRepository accountJpaRepository;

	public AccountRepository(AccountJpaRepository accountJpaRepository) {
		this.accountJpaRepository = accountJpaRepository;
	}

	public Optional<Account> findById(int id) {
		return accountJpaRepository.findById(id);
	}
}

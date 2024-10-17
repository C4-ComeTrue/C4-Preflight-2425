package org.c4marathon.assignment.account.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {
	private final CrudRepository<Account, Integer> accountCrudRepository;

	public AccountRepository(CrudRepository<Account, Integer> accountCrudRepository) {
		this.accountCrudRepository = accountCrudRepository;
	}

	public Optional<Account> findById(int id) {
		return accountCrudRepository.findById(id);
	}
}

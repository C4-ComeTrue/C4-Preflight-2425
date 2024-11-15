package org.c4marathon.assignment.repository;

import java.util.List;

import org.c4marathon.assignment.entity.Account;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
	private final AccountJpaRepository accountJpaRepository;

	public List<Account> findAllAccountByUserId(Integer userId) {
		return accountJpaRepository.findAllByUserId(userId);
	}
}

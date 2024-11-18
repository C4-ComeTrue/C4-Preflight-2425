package org.c4marathon.assignment.domain.account.repository;

import java.util.List;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountReader {
	private final AccountRepository accountRepository;

	public List<Account> findAllAccountsByUserId(Integer userId) {
		return accountRepository.findByUserId(userId);
	}
}

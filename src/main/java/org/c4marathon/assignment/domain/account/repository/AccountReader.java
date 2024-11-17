package org.c4marathon.assignment.domain.account.repository;

import java.util.List;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountReader {
	private final AccountRepository accountRepository;

	List<Account> findAllAccountsByUserId(Integer userId, Pageable pageable) {
		return accountRepository.findByUserId(userId, pageable);
	}
}

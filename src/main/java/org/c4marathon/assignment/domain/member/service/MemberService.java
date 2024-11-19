package org.c4marathon.assignment.domain.member.service;

import java.util.List;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.c4marathon.assignment.domain.account.repository.AccountReader;
import org.c4marathon.assignment.domain.member.dto.TransactionAccountResponse;
import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.c4marathon.assignment.domain.transaction.repository.TransactionReader;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final AccountReader accountReader;
	private final TransactionReader transactionReader;

	@Transactional(readOnly = true)
	public List<TransactionAccountResponse> findTransactionAccountInfoParallel(Integer memberId, Pageable pageable) {
		List<Account> accounts = accountReader.findAllAccountsByUserId(memberId);

		return accounts.parallelStream()
			.flatMap(account -> {
				List<Transaction> transactions = transactionReader.findAllTransactionsBySenderAccount(
					account.getAccountNumber(), pageable);
				return transactions.stream()
					.map(transaction -> TransactionAccountResponse.of(account, transaction));
			})
			.toList();
	}

	@Transactional(readOnly = true)
	public List<TransactionAccountResponse> findTransactionAccountInfo(Integer memberId, Pageable pageable) {
		List<Account> accounts = accountReader.findAllAccountsByUserId(memberId);

		return accounts.stream()
			.flatMap(account -> {
				List<Transaction> transactions = transactionReader.findAllTransactionsBySenderAccount(
					account.getAccountNumber(), pageable);
				return transactions.stream()
					.map(transaction -> TransactionAccountResponse.of(account, transaction));
			})
			.toList();
	}
}

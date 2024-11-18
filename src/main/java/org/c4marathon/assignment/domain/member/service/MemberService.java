package org.c4marathon.assignment.domain.member.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.c4marathon.assignment.domain.account.repository.AccountReader;
import org.c4marathon.assignment.domain.member.dto.TransactionAccountResponse;
import org.c4marathon.assignment.domain.transaction.entity.Transaction;
import org.c4marathon.assignment.domain.transaction.repository.TransactionReader;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final AccountReader accountReader;
	private final TransactionReader transactionReader;

	@Async
	public CompletableFuture<List<Transaction>> getTransactions(String senderName) {
		return CompletableFuture.completedFuture(transactionReader.findAllTransactionsBySenderAccount(senderName));
	}

	@Transactional(readOnly = true)
	public List<TransactionAccountResponse> findTransactionAccountInfoParallel(Integer memberId, Pageable pageable) {
		List<Account> accounts = accountReader.findAllAccountsByUserId(memberId, pageable);

		List<CompletableFuture<List<TransactionAccountResponse>>> transactionAccountResponses = accounts.stream().map(
			account -> getTransactions(account.getAccountNumber()).thenApply(transactions -> transactions.stream()
				.map(transaction -> TransactionAccountResponse.of(account, transaction))
				.toList())
		).toList();

		CompletableFuture.allOf(
			transactionAccountResponses.toArray(new CompletableFuture[transactionAccountResponses.size()])).join();

		return transactionAccountResponses.stream().map(CompletableFuture::join).flatMap(List::stream).toList();
	}

	@Transactional(readOnly = true)
	public List<TransactionAccountResponse> findTransactionAccountInfo(Integer memberId, Pageable pageable) {
		List<Account> accounts = accountReader.findAllAccountsByUserId(memberId, pageable);
		return accounts.stream().map(account -> {
			List<Transaction> transactions = transactionReader.findAllTransactionsBySenderAccount(
				account.getAccountNumber());
			return transactions.stream()
				.map(transaction -> TransactionAccountResponse.of(account, transaction))
				.toList();
		}).flatMap(List::stream).toList();
	}
}

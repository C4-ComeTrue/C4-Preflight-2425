package org.c4marathon.assignment.service;

import java.util.ArrayList;
import java.util.List;

import org.c4marathon.assignment.core.C4ThreadPoolExecutor;
import org.c4marathon.assignment.dto.response.AccountRes;
import org.c4marathon.assignment.dto.response.FinancialInfoRes;
import org.c4marathon.assignment.dto.response.TransactionRes;
import org.c4marathon.assignment.entity.Transaction;
import org.c4marathon.assignment.entity.User;
import org.c4marathon.assignment.exception.BadRequestException;
import org.c4marathon.assignment.repository.AccountRepository;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.c4marathon.assignment.repository.UserRepository;
import org.c4marathon.assignment.util.C4QueryExecuteTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MarathonService {
	public static final int LIMIT_SIZE = 1000;
	public static final int CORE_POOL_SIZE = 8;
	public static final int MAX_POOL_SIZE = 32;
	private final UserRepository userRepository;
	private final AccountRepository accountRepository;
	private final TransactionRepository transactionRepository;
	private final C4ThreadPoolExecutor threadPoolExecutor = new C4ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE);

	public FinancialInfoRes getAllInfoWithLimit(Integer userId) {
		User user = userRepository.findUserById(userId)
			.orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

		List<AccountRes> accountResList = accountRepository.findAllAccountByUserId(user.getId())
			.stream().map(AccountRes::new).toList();

		List<TransactionRes> transactionResList = new ArrayList<>();

		threadPoolExecutor.init();

		accountResList.forEach(accountRes -> threadPoolExecutor.execute(() -> {
			C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursor(LIMIT_SIZE,
				lastTransaction -> transactionRepository.findTransactionByAccountNumberAndLastTransactionId(
					accountRes.accountNumber(), lastTransaction == null ? null : lastTransaction.getId(),
					LIMIT_SIZE),
				transactions -> transactionResList.addAll(
					transactions.stream().map(TransactionRes::new).toList())
			);
		}));

		threadPoolExecutor.waitToEnd();

		return new FinancialInfoRes(accountResList, transactionResList);
	}

	public FinancialInfoRes getAllInfoNotParallel(Integer userId) {
		User user = userRepository.findUserById(userId)
			.orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

		List<AccountRes> accountResList = accountRepository.findAllAccountByUserId(user.getId())
			.stream().map(AccountRes::new).toList();

		List<List<TransactionRes>> transactionRes = accountResList.stream()
			.map(accountRes -> {
				List<TransactionRes> transactionResList = new ArrayList<>();

				C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursor(LIMIT_SIZE,
					lastTransaction -> transactionRepository.findTransactionByAccountNumberAndLastTransactionId(
						accountRes.accountNumber(), lastTransaction == null ? null : lastTransaction.getId(),
						LIMIT_SIZE),
					transactions -> transactionResList.addAll(
						transactions.stream().map(TransactionRes::new).toList())
				);
				return transactionResList;
			})
			.toList();

		List<TransactionRes> transactions = transactionRes.stream()
			.flatMap(List::stream)
			.toList();

		return new FinancialInfoRes(accountResList, transactions);
	}
}
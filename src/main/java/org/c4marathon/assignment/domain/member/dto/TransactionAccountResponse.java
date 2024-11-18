package org.c4marathon.assignment.domain.member.dto;

import java.time.LocalDateTime;

import org.c4marathon.assignment.domain.account.entity.Account;
import org.c4marathon.assignment.domain.transaction.entity.Transaction;

public record TransactionAccountResponse(
	Integer accountId,
	String accountNumber,
	Character accountType,
	Long accountBalance,
	LocalDateTime createdDate,
	LocalDateTime recentTransactionDate,
	Integer transactionId,
	String senderAccount,
	String receiverAccount,
	String senderSwiftCode,
	String receiverSwiftCode,
	String senderName,
	String receiverName,
	Long transactionAmount,
	LocalDateTime transactionDate
) {

	public static TransactionAccountResponse of(Account account, Transaction transaction) {
		return new TransactionAccountResponse(
			account.getId(),
			account.getAccountNumber(),
			account.getAccountType(),
			account.getBalance(),
			account.getCreatedDate(),
			account.getRecentTransactionDate(),
			transaction.getId(),
			transaction.getSenderAccount(),
			transaction.getReceiverAccount(),
			transaction.getSenderSwiftCode(),
			transaction.getReceiverSwiftCode(),
			transaction.getSenderName(),
			transaction.getReceiverName(),
			transaction.getAmount(),
			transaction.getTransactionDate()
		);
	}
}

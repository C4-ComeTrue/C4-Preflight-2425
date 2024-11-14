package org.c4marathon.assignment.dto.response;

import java.time.Instant;

import org.c4marathon.assignment.entity.Transaction;

public record TransactionRes(
	String senderAccount,
	String receiverAccount,
	String senderSwiftCode,
	String receiverSwiftCode,
	String senderName,
	String receiverName,
	Long amount,
	String memo,
	Instant transactionDate
) {
	public TransactionRes(Transaction transaction) {
		this(
			transaction.getSenderAccount(),
			transaction.getReceiverAccount(),
			transaction.getSenderSwiftCode(),
			transaction.getReceiverSwiftCode(),
			transaction.getSenderName(),
			transaction.getReceiverName(),
			transaction.getAmount(),
			transaction.getMemo(),
			transaction.getTransactionDate()
		);
	}
}

package org.c4marathon.assignment.dto.response;

import org.c4marathon.assignment.entity.Account;

public record AccountRes(
	String accountNumber,
	Long balance
) {
	public AccountRes(Account account) {
		this(
			account.getAccountNumber(),
			account.getBalance()
		);
	}
}
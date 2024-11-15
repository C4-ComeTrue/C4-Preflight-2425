package org.c4marathon.assignment.dto.response;

import java.util.List;

public record FinancialInfoRes(
	List<AccountRes> accountRes,
	List<TransactionRes> transactionRes,
	int accountTotal,
	int transactionTotal
) {
	public FinancialInfoRes(List<AccountRes> accountRes, List<TransactionRes> transactionRes) {
		this(
			accountRes,
			transactionRes,
			accountRes.size(),
			transactionRes.size()
		);
	}
}
package org.c4marathon.assignment.domain.member.presentation;

import java.util.List;

import org.c4marathon.assignment.domain.member.dto.TransactionAccountResponse;
import org.c4marathon.assignment.domain.member.service.MemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
	private final MemberService memberService;

	@GetMapping("/v1/members/{memberId}/parallel")
	public ResponseEntity<List<TransactionAccountResponse>> findTransactionAccountInfoParallel(
		@PathVariable("memberId") Integer memberId,
		@PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(memberService.findTransactionAccountInfoParallel(memberId, pageable));
	}

	@GetMapping("/v1/members/{memberId}")
	public ResponseEntity<List<TransactionAccountResponse>> findTransactionAccountInfo(
		@PathVariable("memberId") Integer memberId,
		@PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(memberService.findTransactionAccountInfo(memberId, pageable));
	}
}

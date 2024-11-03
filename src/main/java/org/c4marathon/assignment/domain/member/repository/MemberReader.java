package org.c4marathon.assignment.domain.member.repository;

import org.c4marathon.assignment.domain.member.entity.Member;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberReader {
	private final MemberRepository memberRepository;

	public Member find(Long memberId) {
		return memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("해당 계정은 존재하지 않습니다."));
	}
}

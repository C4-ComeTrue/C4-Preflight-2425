package org.c4marathon.assignment.domain.member.repository;

import java.util.Optional;

import org.c4marathon.assignment.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findById(Long memberId);
}

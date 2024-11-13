package org.c4marathon.assignment.repository;

import java.util.Optional;

import org.c4marathon.assignment.entity.User;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
	private final UserJpaRepository userJpaRepository;

	public Optional<User> findUserByEmail(String email) {
		return userJpaRepository.findByEmail(email);
	}
}

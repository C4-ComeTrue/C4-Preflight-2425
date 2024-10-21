package org.c4marathon.assignment.user.domain;

import java.util.Optional;

import org.c4marathon.assignment.user.infrastructure.UserJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
	private final UserJpaRepository userJpaRepository;

	public UserRepository(UserJpaRepository userJpaRepository) {
		this.userJpaRepository = userJpaRepository;
	}

	public Optional<User> findById(int id) {
		return userJpaRepository.findById(id);
	}
}

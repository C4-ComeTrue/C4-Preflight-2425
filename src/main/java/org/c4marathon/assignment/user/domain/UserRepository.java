package org.c4marathon.assignment.user.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
	private final CrudRepository<User, Integer> userCrudRepository;

	public UserRepository(CrudRepository<User, Integer> userCrudRepository) {
		this.userCrudRepository = userCrudRepository;
	}

	public Optional<User> findById(int id) {
		return userCrudRepository.findById(id);
	}
}

package org.c4marathon.assignment.repository;

import java.util.List;

import org.c4marathon.assignment.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, Long> {
	List<Account> findAllByUserId(Integer userId);
}

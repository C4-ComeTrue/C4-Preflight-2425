package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.controller.AccountCreateRequest;
import org.c4marathon.assignment.domain.Account;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final AccountJpaRepository accountJpaRepository;

    public void save(AccountCreateRequest request){
        accountJpaRepository.save(new Account(request.accountNumber(), request.userId(), request.accountType(), request.memo(), 0L, Instant.now(), Instant.now()));
    }

}

package org.c4marathon.assignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.controller.AccountCreateRequest;
import org.c4marathon.assignment.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount(AccountCreateRequest accountCreateRequest){
        accountRepository.save(accountCreateRequest);

        //1. 이메일을 보낼 테이블에 저장이 된다. (메일 저장 테이블)
        //2. Cron으로 테이블 내용 기반으로 fixedRate로 메일 전송
        //  - 상세하게는 Delayed, Failed 묶어서 진행
    }
}

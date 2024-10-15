package org.c4marathon.assignment.controller;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.common.exception.enums.SuccessCode;
import org.c4marathon.assignment.common.model.SuccessResponse;
import org.c4marathon.assignment.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    public SuccessResponse postAccount(@RequestBody AccountCreateRequest accountCreateRequest){
        accountService.createAccount(accountCreateRequest);
        return SuccessResponse.success(SuccessCode.CREATE_ACCOUNT_SUCCESS);
    }
}

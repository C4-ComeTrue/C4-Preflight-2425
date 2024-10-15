package org.c4marathon.assignment.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    CREATE_ACCOUNT_SUCCESS(HttpStatus.OK, "계좌 생성 성공");

    private final HttpStatus httpStatus;
    private final String message;
}

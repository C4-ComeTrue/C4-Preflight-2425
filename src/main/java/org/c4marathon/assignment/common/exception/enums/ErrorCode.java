package org.c4marathon.assignment.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    INVALID_VALUE_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "유효하지 않은 값타입을 입력했습니다."),

    // 405
    METHOD_NOT_ALLOWED_EXCEPTION(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 메소드 입니다."),

    // 409
    ALREADY_EXIST_ACCOUNT_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 계좌입니다."),

    // 500
    INTERNAL_SERVER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

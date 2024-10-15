package org.c4marathon.assignment.common.exception.model;

import lombok.Getter;
import org.c4marathon.assignment.common.exception.enums.ErrorCode;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

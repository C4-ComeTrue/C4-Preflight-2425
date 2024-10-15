package org.c4marathon.assignment.common.exception.model;

import org.c4marathon.assignment.common.exception.enums.ErrorCode;

public class BadRequestException extends BaseException {
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}

package org.c4marathon.assignment.common.exception.model;

import org.c4marathon.assignment.common.exception.enums.ErrorCode;

public class ConflictException extends BaseException {
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}

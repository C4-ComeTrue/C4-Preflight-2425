package org.c4marathon.assignment.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.common.exception.model.ConflictException;
import org.c4marathon.assignment.common.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;



import static org.c4marathon.assignment.common.exception.enums.ErrorCode.INTERNAL_SERVER_EXCEPTION;
import static org.c4marathon.assignment.common.exception.enums.ErrorCode.INVALID_VALUE_TYPE_EXCEPTION;
import static org.c4marathon.assignment.common.exception.enums.ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionAdvice {
    /**
     * 400 Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ErrorResponse handleTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return ErrorResponse.error(INVALID_VALUE_TYPE_EXCEPTION);
    }

    /**
     * 405 Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ErrorResponse handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return ErrorResponse.error(METHOD_NOT_ALLOWED_EXCEPTION);
    }

    /**
     * 409 Conflict
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    protected ErrorResponse handleConflictException(final ConflictException e) {
        return ErrorResponse.error(e.getErrorCode());
    }

    /**
     * 500 Internal Server
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ErrorResponse handleException(final Exception error) {
        log.error(error.getMessage(), error);
        return ErrorResponse.error(INTERNAL_SERVER_EXCEPTION);
    }
}

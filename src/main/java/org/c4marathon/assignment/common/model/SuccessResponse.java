package org.c4marathon.assignment.common.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.c4marathon.assignment.common.exception.enums.SuccessCode;

import java.util.Optional;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {
    private final int code;
    private final String message;
    private final Optional<T> data;

    public static <T> SuccessResponse<T> success(SuccessCode successCode, T data) {
        return new SuccessResponse<>(successCode.getHttpStatus().value(), successCode.getMessage(), Optional.ofNullable(data));
    }

    public static <T> SuccessResponse<T> success(SuccessCode successCode) {
        return new SuccessResponse<>(successCode.getHttpStatus().value(), successCode.getMessage(),Optional.empty());
    }
}


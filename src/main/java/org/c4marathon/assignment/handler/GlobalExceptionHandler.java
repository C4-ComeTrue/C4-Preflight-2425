package org.c4marathon.assignment.handler;

import java.util.HashMap;
import java.util.Map;

import org.c4marathon.assignment.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	private static final String MESSAGE = "message";

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put(MESSAGE, ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultMap);
	}
}
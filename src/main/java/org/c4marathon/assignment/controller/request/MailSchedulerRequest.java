package org.c4marathon.assignment.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MailSchedulerRequest(
	int userId,
	@Size(max = 30)
	@NotNull
	@Email
	String email,
	@Size(max = 500)
	@NotBlank
	String content
) {
}

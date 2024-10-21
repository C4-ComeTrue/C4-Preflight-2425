package org.c4marathon.assignment.mail.controller;

import org.c4marathon.assignment.mail.application.MailService;
import org.c4marathon.assignment.mail.dto.CreateMailRequestDto;
import org.c4marathon.assignment.mail.dto.CreateMailResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
	private final MailService mailService;

	public MailController(MailService mailService) {
		this.mailService = mailService;
	}

	@PostMapping("/account-create") // 메일함에 쌓아두는 용도
	public ResponseEntity<CreateMailResponseDto> createMail(@RequestBody CreateMailRequestDto createMailRequestDto) {
		CreateMailResponseDto responseDto = mailService.createMail(createMailRequestDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
	}
}

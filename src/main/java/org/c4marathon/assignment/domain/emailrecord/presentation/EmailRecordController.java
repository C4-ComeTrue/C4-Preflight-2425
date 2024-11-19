package org.c4marathon.assignment.domain.emailrecord.presentation;

import java.net.URI;

import org.c4marathon.assignment.domain.emailrecord.dto.SaveEmailRecordRequest;
import org.c4marathon.assignment.domain.emailrecord.entity.EmailRecord;
import org.c4marathon.assignment.domain.emailrecord.service.EmailRecordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EmailRecordController {
	private final EmailRecordService emailRecordService;

	@Value("${api.email-record.base-url}")
	private String baseUrl;

	@PostMapping("/v1/email-records")
	public ResponseEntity<EmailRecord> saveEmailRecord(@Valid SaveEmailRecordRequest request) {
		EmailRecord saved = emailRecordService.save(request);
		URI uri = UriComponentsBuilder.fromUriString(baseUrl + saved.getId())
			.build()
			.toUri();
		return ResponseEntity.created(uri).build();
	}
}

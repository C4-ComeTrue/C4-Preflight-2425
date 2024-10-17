package org.c4marathon.assignment.mail.controller;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.mail.dto.MailRequest;
import org.c4marathon.assignment.mail.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMail(@RequestBody MailRequest request) {
        mailService.saveMailRequest(request);
        return ResponseEntity.ok().build();
    }
}

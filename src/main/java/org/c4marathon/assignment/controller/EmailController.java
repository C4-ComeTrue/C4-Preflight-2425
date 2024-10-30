package org.c4marathon.assignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-log")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping()
    public ResponseEntity<Void> postEmailLog(@RequestBody @Valid PostEmailBoxReq postEmailBoxReq) {
        emailService.postEmailLog(postEmailBoxReq);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

package org.c4marathon.assignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.controller.request.MailSchedulerRequest;
import org.c4marathon.assignment.service.MailSchedulerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/backoffice")
@RequiredArgsConstructor
public class MailSchedulerController {

    private final MailSchedulerService mailSchedulerService;

    /**
     * Step 1. 메일 전송 요청을 남기는 API 입니다.
     */
    @PostMapping("/mail")
    public void postMailScheduler(@RequestBody @Valid MailSchedulerRequest request) {
        mailSchedulerService.mailLogging(request);
    }

}

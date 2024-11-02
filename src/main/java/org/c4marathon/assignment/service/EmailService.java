package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.repository.EmailBoxRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final EmailBoxRepository emailBoxRepository;

    public void postEmailBox(PostEmailBoxReq postEmailBoxReq) {
        emailBoxRepository.postEmailBox(postEmailBoxReq);
    }
}

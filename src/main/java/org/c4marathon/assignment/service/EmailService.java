package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.repository.EmailBoxRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final EmailBoxRepository emailBoxRepository;

    public void postEmailBox(PostEmailBoxReq postEmailBoxReq) {
        emailBoxRepository.postEmailBox(postEmailBoxReq);
    }
}

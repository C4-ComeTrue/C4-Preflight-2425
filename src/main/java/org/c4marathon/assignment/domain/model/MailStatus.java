package org.c4marathon.assignment.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailStatus {
    PENDING,
    SUCCESS,
    FAIL
}

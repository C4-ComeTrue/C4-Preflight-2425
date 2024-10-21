package org.c4marathon.assignment.mail.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mail_archive_opixxx")
public class MailArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_archive_id")
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MailStatus status;

    private int retryCount;

    private LocalDateTime requestTime;

    private LocalDateTime sentTime;

    @Builder
    private MailArchive(Long accountId, String email, String content, MailStatus status, LocalDateTime requestTime) {
        this.accountId = accountId;
        this.email = email;
        this.content = content;
        this.status = status;
        this.requestTime = requestTime;
    }

    public static MailArchive of(Long accountId, String email, String content, LocalDateTime requestTime) {
        return MailArchive.builder()
                .accountId(accountId)
                .email(email)
                .content(content)
                .status(MailStatus.PENDING)
                .requestTime(requestTime)
                .build();
    }

    public void changeStatusSent() {
        this.status = MailStatus.SENT;
        this.sentTime = LocalDateTime.now();
    }
    public void changeStatusFail() {
        this.status = MailStatus.FAIL;
    }

    public void plusRetryCount() {
        this.retryCount++;
    }

}

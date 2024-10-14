package org.c4marathon.assignment.mail.domain;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
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

    private String content;

    @Enumerated(EnumType.STRING)
    private MailStatus status;

    private LocalDateTime requestTime;

    private LocalDateTime sentTime;

}

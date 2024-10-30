package org.c4marathon.assignment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class EmailBox extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    @Builder
    public EmailBox(String email, String content) {
        this.email = email;
        this.content = content;
        this.status = EmailStatus.PENDING;
    }
}

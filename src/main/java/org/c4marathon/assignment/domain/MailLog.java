package org.c4marathon.assignment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.c4marathon.assignment.controller.request.MailSchedulerRequest;
import org.c4marathon.assignment.domain.model.MailStatus;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "mail_log_hellozo0")
public class MailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_log_id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Size(max = 30)
    @NotNull
    @Column(name = "email", nullable = false, length = 30)
    private String email;

    @Enumerated(EnumType.STRING)
    private MailStatus status;

    @Size(max = 500)
    @Column(name = "content", length = 500)
    @NotBlank
    private String content;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime;

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @NotNull
    @Column(name = "count_RT")
    private Integer countRT;

    public MailLog(Integer id, Integer userId, String email, MailStatus status, String content, Instant createTime, Instant updateTime, Integer countRT) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.status = status;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.countRT = countRT;
    }

    public MailLog(MailSchedulerRequest request) {
        this.userId = request.userId();
        this.email = request.email();
        this.status = MailStatus.PENDING;
        this.content = request.content();
        this.createTime = Instant.now();
        this.updateTime = Instant.now();
        this.countRT = 0;
    }

}

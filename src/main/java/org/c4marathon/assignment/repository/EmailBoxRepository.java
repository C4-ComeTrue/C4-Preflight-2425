package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.c4marathon.assignment.dto.request.PostEmailBoxReq;
import org.c4marathon.assignment.entity.EmailBox;
import org.c4marathon.assignment.entity.EmailStatus;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EmailBoxRepository {
    private final EmailBoxJpaRepository emailBoxJpaRepository;
    private static final int BATCH_SIZE = 500;

    public void postEmailBox(PostEmailBoxReq postEmailBoxReq) {
        emailBoxJpaRepository.save(EmailBox.builder()
                .email(postEmailBoxReq.email())
                .content(postEmailBoxReq.accountDescription())
                .build());
    }

    public List<EmailBox> getEmailBoxesByStatus(EmailStatus emailStatus) {
        return emailBoxJpaRepository.findEmailBoxesByStatus(emailStatus);
    }

    public void updateStatusBySize(EmailStatus emailStatus, List<Long> sentEmailIds) {
        ListUtils.partition(sentEmailIds, BATCH_SIZE).forEach(batch -> {
            emailBoxJpaRepository.updateStatus(emailStatus, batch);
        });
    }
}

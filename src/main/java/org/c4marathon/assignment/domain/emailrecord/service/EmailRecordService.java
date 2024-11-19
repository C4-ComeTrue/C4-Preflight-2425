package org.c4marathon.assignment.domain.emailrecord.service;

import static org.c4marathon.assignment.common.configuration.AsyncConfig.*;

import org.c4marathon.assignment.domain.emailrecord.dto.SaveEmailRecordRequest;
import org.c4marathon.assignment.domain.emailrecord.entity.EmailRecord;
import org.c4marathon.assignment.domain.emailrecord.event.EmailRecordEvent;
import org.c4marathon.assignment.domain.emailrecord.query.EmailRecordQueryResult;
import org.c4marathon.assignment.domain.emailrecord.repository.EmailRecordReader;
import org.c4marathon.assignment.domain.emailrecord.repository.EmailRecordStore;
import org.c4marathon.assignment.domain.member.repository.MemberReader;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailRecordService {
	private final EmailRecordStore emailRecordStore;
	private final EmailRecordReader emailRecordReader;
	private final MemberReader memberReader;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public EmailRecord save(SaveEmailRecordRequest request) {
		var member = memberReader.find(request.memberId());
		return emailRecordStore.store(request.toEntity(member));
	}

	@Scheduled(cron = "0 * * * * *")
	@Transactional(readOnly = true)
	public void sendEmail() {
		emailRecordReader.findAllPending()
			.map(result -> {
				setStatusToProcessing(result);
				return EmailRecordEvent.toEvent(result);
			})
			.forEach(eventPublisher::publishEvent);
	}

	@Async(value = ASYNC_THREAD_POOL_NAME)
	@Transactional
	public void setStatusToProcessing(EmailRecordQueryResult result) {
		EmailRecord emailRecord = emailRecordReader.find(result.getId());
		emailRecord.setStatusToProcessing();
	}
}

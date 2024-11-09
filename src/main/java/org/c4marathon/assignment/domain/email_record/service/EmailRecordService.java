package org.c4marathon.assignment.domain.email_record.service;

import static org.c4marathon.assignment.common.configuration.AsyncConfig.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.c4marathon.assignment.domain.email_record.dto.EmailRecordRequest;
import org.c4marathon.assignment.domain.email_record.entity.EmailRecord;
import org.c4marathon.assignment.domain.email_record.event.EmailRecordEvent;
import org.c4marathon.assignment.domain.email_record.query.EmailRecordQueryResult;
import org.c4marathon.assignment.domain.email_record.repository.EmailRecordReader;
import org.c4marathon.assignment.domain.email_record.repository.EmailRecordStore;
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
	public EmailRecord save(EmailRecordRequest.Save request) {
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

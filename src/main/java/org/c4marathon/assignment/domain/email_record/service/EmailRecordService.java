package org.c4marathon.assignment.domain.email_record.service;

import org.c4marathon.assignment.domain.email_record.dto.EmailRecordRequest;
import org.c4marathon.assignment.domain.email_record.repository.EmailRecordStore;
import org.c4marathon.assignment.domain.member.repository.MemberReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailRecordService {
	private final EmailRecordStore emailRecordStore;
	private final MemberReader memberReader;

	@Transactional
	public void save(EmailRecordRequest.Save request) {
		var member = memberReader.find(request.memberId());
		emailRecordStore.store(request.toEntity(member));
	}
}

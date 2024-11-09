package org.c4marathon.assignment.domain.email_record.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRecordQueryResult {
	private Long emailRecordId;
	private String email;
	private String subject;
	private String content;
}

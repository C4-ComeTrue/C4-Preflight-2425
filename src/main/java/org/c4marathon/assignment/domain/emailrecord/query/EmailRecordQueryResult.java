package org.c4marathon.assignment.domain.emailrecord.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailRecordQueryResult {
	private Long id;
	private String email;
	private String subject;
	private String content;
}

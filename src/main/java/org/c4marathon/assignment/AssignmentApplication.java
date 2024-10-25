package org.c4marathon.assignment;

import org.c4marathon.assignment.service.CumulativeAmountService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;


@EnableScheduling
@EnableAsync
@SpringBootApplication
public class AssignmentApplication {

	public static void main(String[] args) {
//		ApplicationContext context = SpringApplication.run(AssignmentApplication.class, args);
//
//		// CumulativeAmountService 빈 가져오기
//		CumulativeAmountService cumulativeAmountService = context.getBean(CumulativeAmountService.class);
//
//		// 2024-01-05T04:00:00 UTC 고정
//		Clock fixedClock = Clock.fixed(Instant.parse("2023-01-05T03:50:00Z"), ZoneOffset.UTC);
//		cumulativeAmountService.setClock(fixedClock);
//
//		// 스케줄러 실행
//		cumulativeAmountService.cumulativeScheduler();

		SpringApplication.run(AssignmentApplication.class, args);
	}

}

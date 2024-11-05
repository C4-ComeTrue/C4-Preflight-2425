package org.c4marathon.assignment.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.c4marathon.assignment.dto.response.StatisticRes;
import org.c4marathon.assignment.repository.StatisticRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticService {
	private final StatisticRepository statisticRepository;

	public List<StatisticRes> getStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
		Instant startInstantDate = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant endInstantDate = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

		return statisticRepository.findAllByStatisticDateBetween(startInstantDate, endInstantDate)
			.stream()
			.map(StatisticRes::new)
			.toList();
	}
}

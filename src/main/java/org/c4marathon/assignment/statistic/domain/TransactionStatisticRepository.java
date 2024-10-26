package org.c4marathon.assignment.statistic.domain;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.c4marathon.assignment.statistic.infrastructure.TransactionStatisticJpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionStatisticRepository {
	private final TransactionStatisticJpaRepository statisticJpaRepository;
	private final JdbcTemplate jdbcTemplate;

	public TransactionStatisticRepository(TransactionStatisticJpaRepository statisticJpaRepository,
		JdbcTemplate jdbcTemplate) {
		this.statisticJpaRepository = statisticJpaRepository;
		this.jdbcTemplate = jdbcTemplate;
	}

	public Optional<TransactionStatistic> findCloseStatisticByStatisticDate(Instant theDay) {
		return statisticJpaRepository.findCloseStatisticByStatisticDate(theDay);
	}

	public void save(TransactionStatistic transactionStatistic) {
		statisticJpaRepository.save(transactionStatistic);
	}

	public void saveAll(List<TransactionStatistic> transactionStatistics) {
		String sql = """
			INSERT INTO transaction_statistic_zzamba (statistic_date, daily_total_amount, cumulative_amount)
			VALUES (?, ?, ?)
			""";

		jdbcTemplate.batchUpdate(sql, transactionStatistics, transactionStatistics.size(), (ps, statistic) -> {
			ps.setObject(1, statistic.getStatisticDate());
			ps.setLong(2, statistic.getDailyTotalAmount());
			ps.setLong(3, statistic.getCumulativeAmount());
		});
	}
}

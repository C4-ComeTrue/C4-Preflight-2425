package org.c4marathon.assignment.statistic.domain;

import java.time.Instant;
import java.util.List;

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

	public void save(TransactionStatistic statistic) {
		statisticJpaRepository.save(statistic);
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

	public List<TransactionStatistic> findAll(Instant start, Instant end) {
		return statisticJpaRepository.findByStatisticDateBetween(start, end);
	}
}

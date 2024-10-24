package org.c4marathon.assignment.statistic.infrastructure;

import org.c4marathon.assignment.statistic.domain.TransactionStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionStatisticJpaRepository extends JpaRepository<TransactionStatistic, Long> {
}

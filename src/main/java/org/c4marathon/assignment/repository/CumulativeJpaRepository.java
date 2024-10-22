package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.CumulativeAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CumulativeJpaRepository extends JpaRepository<CumulativeAmount, Integer> {
}

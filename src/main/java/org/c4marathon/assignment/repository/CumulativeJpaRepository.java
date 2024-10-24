package org.c4marathon.assignment.repository;

import org.c4marathon.assignment.domain.CumulativeAmount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CumulativeJpaRepository extends JpaRepository<CumulativeAmount, Integer> {

    @Query("SELECT c FROM CumulativeAmount c WHERE c.date = :date")
    CumulativeAmount findByDate(@Param("date") LocalDate date);
}

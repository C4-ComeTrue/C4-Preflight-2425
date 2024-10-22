package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CumulativeRepository {
    private final CumulativeJpaRepository cumulativeJpaRepository;
}

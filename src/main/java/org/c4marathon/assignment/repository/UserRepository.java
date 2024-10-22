package org.c4marathon.assignment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    final UserJpaRepository userJpaRepository;

    public boolean existsById(int id){
        return userJpaRepository.existsById(id);
    }

}

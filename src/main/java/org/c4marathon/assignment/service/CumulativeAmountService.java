package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.controller.request.DateRequest;
import org.c4marathon.assignment.controller.request.RangeDateRequest;
import org.c4marathon.assignment.controller.response.CumulativeAmountResponse;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class CumulativeAmountService {
    private final TransactionRepository transactionRepository;

    public CumulativeAmountResponse getCumulativeAmountDate(DateRequest request){

        CumulativeAmountResponse c= new CumulativeAmountResponse(Instant.now(),Instant.now(),1,1);
        return c;
    }

    public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(RangeDateRequest request){

        CumulativeAmountResponse c= new CumulativeAmountResponse(Instant.now(),Instant.now(),1,1);

        return List.of(c);
    }
}

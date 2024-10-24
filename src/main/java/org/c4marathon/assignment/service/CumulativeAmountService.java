package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.controller.request.RangeDateRequest;
import org.c4marathon.assignment.controller.response.CumulativeAmountResponse;
import org.c4marathon.assignment.domain.CumulativeAmount;
import org.c4marathon.assignment.domain.Transaction;
import org.c4marathon.assignment.repository.CumulativeRepository;
import org.c4marathon.assignment.repository.TransactionRepository;
import org.c4marathon.assignment.util.C4QueryExecuteTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class CumulativeAmountService {
    private final TransactionRepository transactionRepository;
    private final CumulativeRepository cumulativeRepository;

    /**
     * Transaction 거래 내역 List로 페이징 검색하는 로직
     *
     * @return
     */
    public CumulativeAmountResponse getCumulativeAmountDate(LocalDate date){

        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);
        if(cumulativeAmount != null) {
            return new CumulativeAmountResponse(cumulativeAmount);
        }

        Instant endDate = date.atStartOfDay(ZoneOffset.UTC) // 시작 시간으로 변환
                .plusDays(1) // 하루를 더하여 다음 날로 이동
                .toInstant();

        C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(
                -1, 1000,
                lastTransaction -> transactionRepository.findTransaction(
                        endDate,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? null : lastTransaction.getId(),
                        1000
                ),
                this::cumulativeAmountByDate
        );

        return new CumulativeAmountResponse(cumulativeRepository.findByDate(date));
    }

    static long count;

    /**
     * Transaction 거래 내역 List로 받아서 통계를 내는 로직
     * */
    private void cumulativeAmountByDate(List<Transaction> transactionList) {

        transactionList.forEach(transaction ->
                log.info("Transaction Date: {}, Amount: {}", transaction.getTransactionDate(), transaction.getAmount())
        );

        count += transactionList.stream().count();
        log.info("Total Transactions Count: {}", count);

        Map<LocalDate, Long> map = transactionList.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate()
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate(),
                        Collectors.summingLong(Transaction::getAmount)
                ));

        map.forEach(this::updateCumulativeAmountByDate);
    }

    private void updateCumulativeAmountByDate(LocalDate date, long dailyAmount){

        // 디버깅: 누적 금액 및 일별 금액이 올바르게 업데이트 되는지 확인
        log.info("Updating statistics for date: {}. Daily Amount: {}, Cumulative Amount: {}",
                date, dailyAmount, cumulativeRepository.findByDate(date) == null ? dailyAmount : cumulativeRepository.findByDate(date).getCumulativeAmount() + dailyAmount);

        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);

        if (cumulativeAmount == null) {
            cumulativeAmount = new CumulativeAmount(date, dailyAmount, dailyAmount);
        } else {
            cumulativeAmount.update(cumulativeAmount.getDailyAmount() + dailyAmount, cumulativeAmount.getCumulativeAmount() + dailyAmount);
        }

        cumulativeRepository.save(cumulativeAmount);

    }

    public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(RangeDateRequest request){
        CumulativeAmountResponse c= new CumulativeAmountResponse(LocalDate.now(),LocalDate.now(),1,1);
        return List.of(c);
    }

}

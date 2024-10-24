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

        // 1. 해당 날짜의 통계 테이블이 있으면 return
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);
        if(cumulativeAmount != null) {
            return new CumulativeAmountResponse(cumulativeAmount);
        }

        //2. 없을 경우 들어온 입력값의 +1일을 한다 (repository에서의 날짜 비교를 위해)
        Instant endDate = date.atStartOfDay(ZoneOffset.UTC)
                .plusDays(1)
                .toInstant();

        //3. endDate 이전으로, 1000개의 사이즈씩 끊어서 데이터를 조회할것이다.
        // 사실상 pageLimit이 필요하지 않기에 -1을 설정했다.
        // lastDate와 lastId는 1000개씩 끊을때의 기준이 되는 값들이다.
        //해당 1000개씩의 데이터를 cumulativeAmountByDate()함수로 전달한다.
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

        // 개수가 제대로 맞는지 로깅 - 추후 삭제 예정
        transactionList.forEach(transaction ->
                log.info("Transaction Date: {}, Amount: {}", transaction.getTransactionDate(), transaction.getAmount())
        );
        count += transactionList.stream().count();
        log.info("Total Transactions Count: {}", count);

        //1. map에 날짜별로 2023-01-01 형태로, amount를 누적 합한다.
        Map<LocalDate, Long> map = transactionList.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDate()
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate(),
                        Collectors.summingLong(Transaction::getAmount)
                ));

        //2. 해당 묶음을 updateCumulativeAmountByDate()함수에 전달한다.
        map.forEach(this::updateCumulativeAmountByDate);
    }

    /**
     * 1000개씩 넘어오는 데이터들을 update - save 하는 함수
     * */
    private void updateCumulativeAmountByDate(LocalDate date, long dailyAmount){

        // 디버깅: 누적 금액 및 일별 금액이 올바르게 업데이트 되는지 확인 - 추후 삭제 예정
        log.info("Updating statistics for date: {}. Daily Amount: {}, Cumulative Amount: {}",
                date, dailyAmount, cumulativeRepository.findByDate(date) == null ? dailyAmount : cumulativeRepository.findByDate(date).getCumulativeAmount() + dailyAmount);

        //1. 먼저 이미 Repository에 먼저 저장되어 있는 1000개의 묶음 결과가 저장되어 있을 수 있으니 조회한다.
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);

        //2. null이라면 첫번째 transaction이기 때문에 새로운 객체를 생성해서 저장.
        // null이 아니라면 update
        if (cumulativeAmount == null) {
            cumulativeAmount = new CumulativeAmount(date, dailyAmount, dailyAmount);
        } else {
            cumulativeAmount.update(cumulativeAmount.getDailyAmount() + dailyAmount, cumulativeAmount.getCumulativeAmount() + dailyAmount);
        }

        //3. 저장한다.
        cumulativeRepository.save(cumulativeAmount);

    }

    public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(RangeDateRequest request){
        CumulativeAmountResponse c= new CumulativeAmountResponse(LocalDate.now(),LocalDate.now(),1,1);
        return List.of(c);
    }

}

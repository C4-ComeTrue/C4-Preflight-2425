package org.c4marathon.assignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
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
     * API : statics/cumulative
     * transaction의 ~ date(endDate) 까지의 일별/누적금액 계산하는 로직
     * */
    public CumulativeAmountResponse getCumulativeAmountDate(LocalDate date){

        // 1. 해당 날짜의 통계 테이블이 있으면 return
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);
        if(cumulativeAmount != null) {return new CumulativeAmountResponse(cumulativeAmount);}

        //2. 없을 경우 들어온 입력값의 +1일을 한다 (repository에서의 날짜 비교를 위해)
        Instant endDate = date.atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant();

        //3. endDate 이전으로, 1000개의 사이즈씩 끊어서 데이터를 조회할것이다.
        // 사실상 pageLimit이 필요하지 않기에 -1을 설정했다.
        // lastDate와 lastId는 1000개씩 끊을때의 기준이 되는 값들이다.
        // 해당 1000개씩의 데이터를 cumulativeAmountByDate()함수로 전달한다.
        C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(
                -1, 1000,
                lastTransaction -> transactionRepository.findTransaction(
                        endDate,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? null : lastTransaction.getId(),
                        1000
                ),
                this::dailyAmountByDate
        );

        //4. 누적값은 따로 계산 - 계산 결과 불일치가 계속되어 우선 분리 구현
        cumulativeAmountByDate(date);

        return new CumulativeAmountResponse(cumulativeRepository.findByDate(date));
    }

    /**
     * API : statics/cumulative/range
     * start ~ endDate 사이의 일별/누적 금액 다시 계산 하는 로직
     * */
    public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(LocalDate startDate, LocalDate endDate){
        List<CumulativeAmountResponse> cumulativeAmountResponses = new ArrayList<>();

        // 첫날의 CumulativeAmount를 가져옴 (첫날 데이터는 무조건 있다고 전제)
        CumulativeAmount previousCumulativeAmount = cumulativeRepository.findByDate(startDate);
        long previousCumulativeTotal = previousCumulativeAmount.getDailyAmount();

        // 첫날의 누적합을 결과 리스트에 추가
        cumulativeAmountResponses.add(new CumulativeAmountResponse(
                startDate,
                startDate,
                previousCumulativeAmount.getDailyAmount(),
                previousCumulativeTotal));

        // startDate 다음 날부터 endDate까지 반복하면서 누적합을 계산
        for (LocalDate currentDate = startDate.plusDays(1); !currentDate.isAfter(endDate); currentDate = currentDate.plusDays(1)) {
            // 현재 날짜의 CumulativeAmount를 가져옴 (dailyAmount는 항상 존재한다고 가정)
            CumulativeAmount currentCumulativeAmount = cumulativeRepository.findByDate(currentDate);
            long dailyAmount = currentCumulativeAmount.getDailyAmount();

            // 전날 누적합 + 오늘의 dailyAmount를 오늘의 누적합으로 설정
            long currentCumulativeTotal = previousCumulativeTotal + dailyAmount;

            // 오늘의 데이터를 리스트에 추가
            cumulativeAmountResponses.add(new CumulativeAmountResponse(
                    currentDate,
                    currentDate,
                    dailyAmount,
                    currentCumulativeTotal));

            // 다음 날을 위한 누적합 업데이트
            previousCumulativeTotal = currentCumulativeTotal;
        }

        // 결과 리스트 반환
        return cumulativeAmountResponses;
    }

    /**
     * API : statics/cumulative
     * > getCumulativeAmountDate > cumulativeAmountByDate
     * 해당 날짜까지의 누적 합 계산 로직
     */
    private void cumulativeAmountByDate(LocalDate date){
        //1. 가장 이른 날짜 가져와서 반복문 돌릴 예정
        Instant earliestTransactionDate = transactionRepository.findEarliestTransactionDate();
        LocalDate startDate = earliestTransactionDate.atZone(ZoneOffset.UTC).toLocalDate();

        long previousCumulativeTotal = 0L;
        // 2. 첫날의 데이터를 처리
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(startDate);
        cumulativeAmount.updateCumulativeAmount(cumulativeAmount.getDailyAmount());
        previousCumulativeTotal = cumulativeAmount.getCumulativeAmount();
        cumulativeRepository.save(cumulativeAmount);

        // 3. startDate의 다음 날부터 주어진 date까지의 데이터를 처리
        //여기서부터 startDate부터 ~ date(주어진 날짜까지의 Date) 까지 일단 startDate는 첫날이라서 그날의 dailyAmount가 저장되고
        // 그 이후 부터는 전날의 CumulativeAmount + 다음날의 dailtAMount 한 누적 금액이 저장이 되는 구조
        for (LocalDate currentDate = startDate.plusDays(1); !currentDate.isAfter(date); currentDate = currentDate.plusDays(1)) {

            // 현재 날짜의 누적합 계산 및 저장
            CumulativeAmount currentDayDailyAmount = cumulativeRepository.findByDate(currentDate);
            currentDayDailyAmount.updateCumulativeAmount(previousCumulativeTotal + currentDayDailyAmount.getDailyAmount());
            previousCumulativeTotal = currentDayDailyAmount.getCumulativeAmount();
            // 데이터 저장
            cumulativeRepository.save(currentDayDailyAmount);
        }

    }

    /**
     * API : statics/cumulative
     * > getCumulativeAmountDate > dailyAmountByDate
     * [1000개 단위로 묶인 데이터로 작업] 당일 Amount만 계산하는  로직
     * */
    private void dailyAmountByDate(List<Transaction> transactionList) {

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
     * API : statics/cumulative
     * > getCumulativeAmountDate > dailyAmountByDate > updateCumulativeAmountByDate
     * [1000개 단위로 묶인 데이터로 작업] 데이터들을 update - save 하는 함수
     * */
    private void updateCumulativeAmountByDate(LocalDate date, long dailyAmount){

        //1. 먼저 이미 Repository에 먼저 저장되어 있는 1000개의 묶음 결과가 저장되어 있을 수 있으니 조회한다.
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);

        //2. null이라면 첫번째 transaction이기 때문에 새로운 객체를 생성해서 저장.
        if (cumulativeAmount == null) {
            cumulativeAmount = new CumulativeAmount(date, dailyAmount, 0L);
        } else {
            cumulativeAmount.updateDailyAmount(cumulativeAmount.getDailyAmount() + dailyAmount);
        }

        cumulativeRepository.save(cumulativeAmount);
    }
}

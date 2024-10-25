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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
     */
    public CumulativeAmountResponse getCumulativeAmountDate(LocalDate date) {

        // 1. 해당 날짜의 통계 테이블이 있으면 return
        CumulativeAmount cumulativeAmount = cumulativeRepository.findByDate(date);
        if (cumulativeAmount != null) {
            return new CumulativeAmountResponse(cumulativeAmount);
        }

        //2. 처음부터 계산 예정
        Instant earliestTransactionDate = transactionRepository.findEarliestTransactionDate(); //1월 1일
        LocalDate currentDate = earliestTransactionDate.atZone(ZoneOffset.UTC).toLocalDate(); //1월 1일

        //3. 하루마다 로직 수행 -->  변수두고 누적합 하니 수행시간이 10분 걸리던게 1분도 안걸린다
        while (!currentDate.isAfter(date)) {
            // 해당 날짜에 이미 값이 존재하면 패스
            if (cumulativeRepository.findByDate(currentDate) != null) {
                currentDate = currentDate.plusDays(1);
                continue;
            }
            AtomicLong dailyAmount = new AtomicLong();

            // 하루치 트랜잭션 합산 (dailyAmount 계산)
            Instant startOfDay = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant endOfDay = currentDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            dailyAmount.set(0); // 매일 초기화

            C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(
                    -1, 1000,
                    lastTransaction -> transactionRepository.findOneDayTransaction(
                            startOfDay,
                            endOfDay,
                            lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                            lastTransaction == null ? null : lastTransaction.getId(),
                            1000
                    ),
                    transactionList -> {
                        // 트랜잭션 리스트에서 amount 합산하여 dailyAmount 누적
                        dailyAmount.addAndGet(transactionList.stream()
                                .mapToLong(Transaction::getAmount)
                                .sum());
                    }
            );
            //첫날에는 무조건 cumulativeAmount 없음
            if (currentDate.equals(earliestTransactionDate.atZone(ZoneOffset.UTC).toLocalDate())) {
                CumulativeAmount firstCA = new CumulativeAmount(currentDate, dailyAmount.get(), dailyAmount.get());
                cumulativeRepository.save(firstCA);
                continue;
            }
            // 다음날 부터는 있음
            CumulativeAmount beforeCA = cumulativeRepository.findByDate(currentDate.minusDays(1));
            CumulativeAmount nowCA = new CumulativeAmount(currentDate, dailyAmount.get(), beforeCA.getCumulativeAmount() + dailyAmount.get());
            cumulativeRepository.save(nowCA);
        }
        return new CumulativeAmountResponse(cumulativeRepository.findByDate(date));
    }

    /**
     * API : statics/cumulative/range
     * start ~ endDate 사이의 일별/누적 금액 다시 계산 하는 로직
     */
    public List<CumulativeAmountResponse> getCumulativeAmountRangeDate(LocalDate startDate, LocalDate endDate) {
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
     * Scheduler : 새벽 4시 마다, 전날의 통계 집계가 진행된다.
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void cumulativeScheduler() {
        // 어제 날짜 계산
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Instant startDate = yesterday.atStartOfDay(ZoneOffset.UTC).toInstant();// 2023-01-04-00:00
        Instant endDate = yesterday.atStartOfDay(ZoneOffset.UTC).plusDays(1).toInstant(); // 2023-01-05-00:00

        //어제 날짜만의 daily 누적합 구하기 = yesterDayDailAmount
        AtomicLong dailAmount = new AtomicLong();
        C4QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(
                -1, 1000,
                lastTransaction -> transactionRepository.findOneDayTransaction(
                        startDate,
                        endDate,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? null : lastTransaction.getId(),
                        1000
                ),
                transactionList -> {
                    // 트랜잭션 리스트에서 amount 합산하여 dailyAmount 누적
                    dailAmount.addAndGet(transactionList.stream()
                            .mapToLong(Transaction::getAmount)
                            .sum());
                }
        );

        LocalDate twoDaysAgo = yesterday.minusDays(1); //3일
        CumulativeAmount twoDaysAgoCumulativeAmount = cumulativeRepository.findByDate(twoDaysAgo); //3일꺼

        long twoDaysAgoCumulativeAmountNum = (twoDaysAgoCumulativeAmount != null) ? twoDaysAgoCumulativeAmount.getCumulativeAmount() : 0L;
        long cumulativeAmount = twoDaysAgoCumulativeAmountNum + dailAmount.get();

        //이거 추가된거
        CumulativeAmount cumulativeAmount1 = new CumulativeAmount(startDate.atZone(ZoneOffset.UTC).toLocalDate(), dailAmount.get(), cumulativeAmount);
        cumulativeRepository.save(cumulativeAmount1);

    }

}

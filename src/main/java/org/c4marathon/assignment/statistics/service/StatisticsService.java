package org.c4marathon.assignment.statistics.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.c4marathon.assignment.statistics.domain.Statistics;
import org.c4marathon.assignment.statistics.domain.repository.StatisticsRepository;
import org.c4marathon.assignment.statistics.dto.StatisticsResponse;
import org.c4marathon.assignment.transaction.domain.Transaction;
import org.c4marathon.assignment.transaction.domain.repository.TransactionRepository;
import org.c4marathon.assignment.util.QueryExecuteTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final int LIMIT_SIZE = 1000;
    private final TransactionRepository transactionRepository;
    private final StatisticsRepository statisticsRepository;

    /**
     * 새벽 4시 전날 통계를 집계
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void scheduleStatistics() {
        calculateScheduleStatistics();
    }

    /**
     * 현재 시간 기준 전날의 통계를 집계
     */
    public void calculateScheduleStatistics() {
        LocalDate date = LocalDate.now().minusDays(1L);
        Long allTransactionAmountSum = transactionRepository.getAllTransactionAmountSum();

        AtomicLong totalRemittanceByDate = new AtomicLong(0L);
        QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(-1, LIMIT_SIZE,
                lastTransaction -> transactionRepository.findTransactionByDate(
                        date,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? 0 : lastTransaction.getId(),
                        1000),
                transactionList -> totalRemittanceByDate.addAndGet(
                        transactionList.stream()
                                .mapToLong(Transaction::getAmount)
                                .sum()

                )
        );
        allTransactionAmountSum += totalRemittanceByDate.get();

        saveOrUpdateStatistics(date, totalRemittanceByDate.get(), allTransactionAmountSum);
    }

    /**
     * Cursor 페이징 기반으로 Transaction 데이터를 1000개씩 pageSize 만큼 조회
     * @param pageSize
     * @param endDate
     */
    @Transactional
    public void calculateStatistics(int pageSize, LocalDate endDate) {

        AtomicLong latestCumulativeRemittance = new AtomicLong(statisticsRepository.getLatestCumulativeRemittance());
        Map<LocalDate, Long> dailyTotalsMap = new TreeMap<>();

        QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(pageSize, LIMIT_SIZE,
                lastTransaction -> transactionRepository.findTransactionByLastDate(
                        endDate,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? 0 : lastTransaction.getId(),
                        1000),
                transactionList -> processTransactionBatch(transactionList, dailyTotalsMap)
        );


        dailyTotalsMap.forEach((date, totalRemittance) -> {
            latestCumulativeRemittance.addAndGet(totalRemittance);
            saveOrUpdateStatistics(date, totalRemittance, latestCumulativeRemittance.get());
        });
    }

    /**
     * 시작날짜와 종료날짜를 입력받아 통계 데이터를 조회
     * @param startDate
     * @param endDate
     * @return
     */
    @Transactional(readOnly = true)
    public List<StatisticsResponse> getStatisticsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {
        List<Statistics> statisticsByDate = statisticsRepository.findByStatisticsByStartDateAndEndDate(startDate, endDate);

        return statisticsByDate.stream()
                .map(StatisticsResponse::new)
                .toList();
    }

    /**
     * 특정 날짜에 대한 통계를 집계하는 비즈니스 로직
     * @param date
     */
    public void calculateStatisticsForDay(LocalDate date) {

        Long allTransactionAmountSum = transactionRepository.getAllTransactionAmountSumBeforeDate(date);

        AtomicLong totalRemittanceByDate = new AtomicLong(0L);
        QueryExecuteTemplate.<Transaction>selectAndExecuteWithCursorAndPageLimit(-1, LIMIT_SIZE,
                lastTransaction -> transactionRepository.findTransactionByDate(
                        date,
                        lastTransaction == null ? null : lastTransaction.getTransactionDate(),
                        lastTransaction == null ? 0 : lastTransaction.getId(),
                        1000),
                transactionList -> totalRemittanceByDate.addAndGet(
                        transactionList.stream()
                                .mapToLong(Transaction::getAmount)
                                .sum()

                )
        );
        allTransactionAmountSum += totalRemittanceByDate.get();

        saveOrUpdateStatistics(date, totalRemittanceByDate.get(), allTransactionAmountSum);
    }



    private void processTransactionBatch(List<Transaction> transactionList, Map<LocalDate, Long> dailyTotalsMap) {

        ZoneId zoneId = ZoneId.of("UTC");

        Map<LocalDate, Long> totalDailyRemittance = transactionList.stream()
                .collect(Collectors.groupingBy(
                        transaction -> LocalDate.ofInstant(transaction.getTransactionDate(), zoneId), // 날짜별 그룹화
                        Collectors.summingLong(Transaction::getAmount) // 날짜별 금액 합계
                ));

        // 이전 Map(dailyTotalsMap)에 현재 Batch의 일별 누적 금액 합산
        totalDailyRemittance.forEach((date, totalRemittance) ->
                dailyTotalsMap.merge(date, totalRemittance, Long::sum)
        );
    }

    /**
     * 계산한 일 단위 송금금액과 누적 송금금액을 통계 데이터에 저장
     * @param transactionDate
     * @param totalRemittance
     * @param latestCumulativeRemittance
     */
    private void saveOrUpdateStatistics(LocalDate transactionDate, Long totalRemittance, Long latestCumulativeRemittance) {

        Statistics statistics = statisticsRepository.findByStatisticsDate(transactionDate);

        if (statistics == null) {
            statistics = Statistics.of(
                    transactionDate,
                    totalRemittance,
                    latestCumulativeRemittance,
                    LocalDateTime.now()
            );
        } else {
            statistics.update(
                    transactionDate,
                    totalRemittance,
                    statistics.getCumulativeRemittance() + totalRemittance,
                    LocalDateTime.now()
            );
        }
        statisticsRepository.save(statistics);

    }

}

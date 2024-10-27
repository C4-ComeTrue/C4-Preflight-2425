package org.c4marathon.assignment.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class QueryExecuteTemplate {

    /*
     * selectFunction -> 데이터 조회해오는 용도
     * resultConsumer -> 조회한 데이터를 이용해서 어떤 비즈니스 로직을 수행하는 용도
     * */
    public static <T> void selectAndExecuteWithCursor(
            int limit,
            Function<T, List<T>> selectFunction,
            Consumer<List<T>> resultConsumer) {

        List<T> resultList = null;
        do {
            resultList = selectFunction.apply(resultList != null ? resultList.get(resultList.size() - 1) : null);
            if (!resultList.isEmpty()) {
                resultConsumer.accept(resultList);
            }
        } while (resultList.size() >= limit);
    }

    /*
     * pageLimit 가 음수이면 실행횟수 제한 없음
     * iterationCount 가 pageLimit 에 도달하면 중단
     * 이 메서드는 테스트 용을 위한 메서드(많은 데이터를 다 조회할 순 없잖아)
     * */
    public static <T> void selectAndExecuteWithCursorAndPageLimit(
            int pageLimit,
            int limit,
            Function<T, List<T>> selectFunction,
            Consumer<List<T>> resultConsumer) {

        if (pageLimit < 0) {
            selectAndExecuteWithCursor(limit, selectFunction, resultConsumer);
            return;
        }

        var iterationCount = 0;
        List<T> resultList = null;
        do {
            resultList = selectFunction.apply(resultList != null ? resultList.get(resultList.size() - 1) : null);
            if (!resultList.isEmpty()) {
                resultConsumer.accept(resultList);
            }
            if (++iterationCount >= pageLimit) {
                break;
            }
        } while (resultList.size() >= limit);
    }
}

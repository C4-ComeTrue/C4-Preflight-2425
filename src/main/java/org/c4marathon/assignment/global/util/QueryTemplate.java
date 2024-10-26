package org.c4marathon.assignment.global.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryTemplate {
	private QueryTemplate() {}

	// limit 만큼 배치 처리
	public static <T> void selectAndExecuteWithCursor(int limit, Function<T, List<T>> selectFunc, Consumer<List<T>> resultFunc) {
		List<T> resultList = null;

		do {
			resultList = selectFunc.apply(resultList == null ? null : resultList.get(resultList.size() - 1));

			if (!resultList.isEmpty()) {
				resultFunc.accept(resultList);
			}
		} while (resultList.size() >= limit);
	}
}

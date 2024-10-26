package org.c4marathon.assignment.model;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.c4marathon.assignment.util.C4PageTokenUtil;

public record PageInfo<T>(
	String pageToken, //커서 정보 (다음 호출 시 사용)
	List<T> data, // 데이터 목록
	boolean hasNext //다음 페이지 존재 여부
) {
	public static <T> PageInfo<T> of(List<T> data, int expectedSize, Function<T, Object> firstPageTokenFunction,
		Function<T, Object> secondPageTokenFunction) {
		if (data.size() <= expectedSize) {
			return new PageInfo<>(null, data, false);
		}

		var lastValue = data.get(expectedSize - 1);
		var pageToken = C4PageTokenUtil.encodePageToken(Pair.of(
			firstPageTokenFunction.apply(lastValue),
			secondPageTokenFunction.apply(lastValue)
		));

		return new PageInfo<>(pageToken, data.subList(0, expectedSize), true);
	}
}

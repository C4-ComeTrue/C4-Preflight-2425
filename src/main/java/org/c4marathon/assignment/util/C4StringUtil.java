package org.c4marathon.assignment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class C4StringUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(C4StringUtil.class);

    /**
     * Object 를 출력 가능한 String 으로 변환
     * @param object 변환 대상 객체
     * @return 구조화 된 문자열
     */
    public static String reflectionToString(Object object) {
        if (object != null) {
            try {
                return OBJECT_MAPPER.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                LOGGER.warn("Failed to serialize object to JSON", e);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 형식과 객체가 주어졌을 때, 해당 형식에 맞춰 객체를 배치한다.
     * @param format
     * @param objects
     * @return
     */
    public static String format(String format, Object... objects) {
        return MessageFormatter.arrayFormat(format, objects).getMessage();
    }
}

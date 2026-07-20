package com.weg.WEGpark.park.internal.app.shared.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class FilterUtil {

    public static boolean checkMoreThanOneFilter (Object dto) {
        if (dto == null) return false;

        long count = Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(dto);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(value -> !(value instanceof String str && str.isBlank()))
                .count();

        return count <= 1;
    }
}

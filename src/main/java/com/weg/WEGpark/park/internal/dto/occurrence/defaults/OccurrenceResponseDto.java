package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import java.time.LocalDateTime;

public record OccurrenceResponseDto(

        Long id,

        LocalDateTime dateHour,

        String location,

        String gate
) {
}

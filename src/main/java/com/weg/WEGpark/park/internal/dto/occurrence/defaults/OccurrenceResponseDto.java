package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;

import java.time.LocalDateTime;

public record OccurrenceResponseDto(

        Long id,

        LocalDateTime dateHour,

        String location,

        String gate,

        OccurrenceType userType
) {
}

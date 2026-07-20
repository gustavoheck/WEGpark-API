package com.weg.WEGpark.park.internal.dto.occurrence.filter;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;

import java.time.YearMonth;

public record FilterOccurrenceRequestDTO(

        YearMonth yearMonth,

        String location,

        String gate,

        OccurrenceType occurrenceType,

        Boolean recents
) {
}

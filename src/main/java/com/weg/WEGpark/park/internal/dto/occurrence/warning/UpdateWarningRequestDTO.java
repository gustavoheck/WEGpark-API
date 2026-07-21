package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;

public record UpdateWarningRequestDTO(
        CreateOccurrenceRequestDto defaults,

        WarningType warningType,

        String description
) {
}

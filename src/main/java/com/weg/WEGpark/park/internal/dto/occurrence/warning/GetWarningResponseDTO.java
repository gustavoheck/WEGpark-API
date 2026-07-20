package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceResponseDto;

public record GetWarningResponseDTO(

        CreateOccurrenceResponseDto defaults,

        WarningType warningType,

        String description
) {
}

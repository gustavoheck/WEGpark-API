package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;

public record GetWarningResponseDTO(

        DefaultOccurrenceResponseDto defaults,

        WarningType warningType,

        String description
) {
}

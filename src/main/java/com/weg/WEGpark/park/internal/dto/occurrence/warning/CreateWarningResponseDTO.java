package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;

public record CreateWarningResponseDTO(
        Long id,

        DefaultOccurrenceResponseDto defaults,

        WarningType warningType,

        String description
) {
}

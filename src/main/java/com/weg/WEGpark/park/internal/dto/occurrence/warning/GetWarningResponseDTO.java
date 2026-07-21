package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;

import java.util.UUID;

public record GetWarningResponseDTO(

        UUID uuid,

        DefaultOccurrenceResponseDto defaults,

        WarningType warningType,

        String description
) {
}

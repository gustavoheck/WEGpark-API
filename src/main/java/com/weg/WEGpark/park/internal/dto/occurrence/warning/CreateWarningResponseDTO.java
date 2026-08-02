package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;

import java.util.UUID;

public record CreateWarningResponseDTO(

        UUID uuid,

        DefaultOccurrenceResponseDTO defaults,

        WarningType warningType,

        String description
) {
}

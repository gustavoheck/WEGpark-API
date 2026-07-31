package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO;
import jakarta.validation.Valid;

public record UpdateWarningRequestDTO(

        @Valid
        CreateOccurrenceRequestDTO defaults,

        WarningType warningType,

        String description
) {
}

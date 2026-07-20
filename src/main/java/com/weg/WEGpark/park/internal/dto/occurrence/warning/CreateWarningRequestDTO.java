package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;

public record CreateWarningRequestDTO(
        OccurrenceRequestDto defaults,

        @NotBlank(message = "The warning type can not be null or blank")
        WarningType warningType,

        @NotBlank(message = "The description can not be null or blank")
        String description
) {
}

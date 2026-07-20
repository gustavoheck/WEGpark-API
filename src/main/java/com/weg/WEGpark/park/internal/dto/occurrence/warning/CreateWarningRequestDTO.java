package com.weg.WEGpark.park.internal.dto.occurrence.warning;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWarningRequestDTO(
        CreateOccurrenceRequestDto defaults,

        @NotNull(message = "The warning type can not be null")
        WarningType warningType,

        @NotBlank(message = "The description can not be null or blank")
        String description
) {
}

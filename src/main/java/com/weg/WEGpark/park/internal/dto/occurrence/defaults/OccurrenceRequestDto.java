package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OccurrenceRequestDto(

        @NotBlank(message = "The location can not be null or blank")
        String location,

        @NotBlank(message = "The gate can not be null or blank")
        String gate
) {
}

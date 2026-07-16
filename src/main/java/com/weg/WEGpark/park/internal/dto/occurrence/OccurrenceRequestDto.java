package com.weg.WEGpark.park.internal.dto.occurrence;

import jakarta.validation.constraints.NotBlank;

public record OccurrenceRequestDto(

        @NotBlank(message = "The location can not be null or blank")
        String location,

        @NotBlank(message = "The gate can not be null or blank")
        String gate
) {
}

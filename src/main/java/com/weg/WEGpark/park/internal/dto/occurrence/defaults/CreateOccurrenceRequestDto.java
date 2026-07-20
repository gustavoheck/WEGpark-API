package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import jakarta.validation.constraints.NotBlank;

public record CreateOccurrenceRequestDto(

        @NotBlank(message = "The location can not be null or blank")
        String location,

        @NotBlank(message = "The gate can not be null or blank")
        String gate
) {
}

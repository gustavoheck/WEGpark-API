package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CreateOccurrenceRequestDto(

        @NotBlank(message = "The location can not be null or blank")
        String location,

        @NotBlank(message = "The gate can not be null or blank")
        String gate,

        @NotBlank(message = "The guard can not be null or blank")
        String guardBadgeNumber,

        @NotBlank(message = "The plate of the car can not be null or blank")
        String plate
) {
}

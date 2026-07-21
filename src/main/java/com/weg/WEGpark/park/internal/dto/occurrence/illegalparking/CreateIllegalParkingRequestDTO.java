package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIllegalParkingRequestDTO(
        CreateOccurrenceRequestDto defaults,

        @NotNull(message = "The parking space type can not be null")
        ParkingSpaceType parkingSpaceType,

        @NotBlank(message = "The description can not be null or blank")
        String description
) {
}

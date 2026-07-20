package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceRequestDto;
import jakarta.validation.constraints.NotBlank;

public record CreateIllegalParkingRequestDTO(
        OccurrenceRequestDto defaults,

        @NotBlank(message = "The parking space type can not be null or blank")
        ParkingSpaceType parkingSpaceType,

        @NotBlank(message = "The description can not be null or blank")
        String description
) {
}

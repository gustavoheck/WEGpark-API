package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceResponseDto;

public record GetIllegalParkingResponseDTO(

        CreateOccurrenceResponseDto defaults,

        ParkingSpaceType parkingSpaceType,

        String description
) {
}

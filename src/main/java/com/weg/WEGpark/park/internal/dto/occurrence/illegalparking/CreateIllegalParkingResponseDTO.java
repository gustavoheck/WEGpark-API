package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceResponseDto;

public record CreateIllegalParkingResponseDTO(
        OccurrenceResponseDto defaults,

        ParkingSpaceType parkingSpaceType,

        String description

) {
}

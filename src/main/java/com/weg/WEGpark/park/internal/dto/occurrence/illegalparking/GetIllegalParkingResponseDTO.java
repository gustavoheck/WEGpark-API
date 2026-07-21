package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;

import java.util.UUID;

public record GetIllegalParkingResponseDTO(

        UUID uuid,

        DefaultOccurrenceResponseDto defaults,

        ParkingSpaceType parkingSpaceType,

        String description
) {
}

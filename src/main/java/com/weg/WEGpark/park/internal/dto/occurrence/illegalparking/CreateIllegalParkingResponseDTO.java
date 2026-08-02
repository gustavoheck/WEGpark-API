package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;

import java.util.UUID;

public record CreateIllegalParkingResponseDTO(

        UUID uuid,

        DefaultOccurrenceResponseDTO defaults,

        ParkingSpaceType parkingSpaceType,

        String description
) {
}

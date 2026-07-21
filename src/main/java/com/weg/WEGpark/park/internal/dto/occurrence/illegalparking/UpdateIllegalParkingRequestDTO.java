package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;

public record UpdateIllegalParkingRequestDTO(

        CreateOccurrenceRequestDto defaults,

        ParkingSpaceType parkingSpaceType,

        String description
) {
}

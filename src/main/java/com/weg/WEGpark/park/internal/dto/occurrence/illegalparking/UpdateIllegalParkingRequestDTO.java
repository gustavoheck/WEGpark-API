package com.weg.WEGpark.park.internal.dto.occurrence.illegalparking;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO;
import jakarta.validation.Valid;

public record UpdateIllegalParkingRequestDTO(

        @Valid
        CreateOccurrenceRequestDTO defaults,

        ParkingSpaceType parkingSpaceType,

        String description
) {
}

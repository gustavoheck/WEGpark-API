package com.weg.WEGpark.park.internal.dto.occurrence.defaults;

import com.weg.WEGpark.park.internal.dto.user.guard.GetGuardResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;

import java.time.LocalDateTime;

public record DefaultOccurrenceResponseDTO(

        LocalDateTime dateHour,

        String location,

        String gate,

        GetVehicleResponseDTO vehicle,

        GetGuardResponseDTO guard

) {
}

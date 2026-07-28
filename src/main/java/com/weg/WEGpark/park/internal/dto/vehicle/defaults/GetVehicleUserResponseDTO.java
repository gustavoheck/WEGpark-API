package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

import java.util.UUID;

public record GetVehicleUserResponseDTO(

        UUID userUuid,

        Boolean isOwner
) {
}

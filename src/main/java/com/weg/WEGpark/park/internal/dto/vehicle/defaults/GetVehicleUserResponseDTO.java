package com.weg.WEGpark.park.internal.dto.vehicle.defaults;

import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;

import java.util.UUID;

public record GetVehicleUserResponseDTO(

        UUID userUuid,

        Boolean isOwner,

        String telephone,

        Boolean associationActive,

        String name,

        ParkUserType userType,

        String badgeNumber,

        String location,

        String boss,

        String company
) {
}

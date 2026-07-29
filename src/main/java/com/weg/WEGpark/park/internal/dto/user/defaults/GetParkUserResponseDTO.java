package com.weg.WEGpark.park.internal.dto.user.defaults;

import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;

import java.util.UUID;

public record GetParkUserResponseDTO(
        UUID uuid,

        String email,

        String telephone,

        String name,

        Boolean active,

        ParkUserType userType
) {
}

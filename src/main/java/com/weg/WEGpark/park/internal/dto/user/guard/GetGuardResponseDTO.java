package com.weg.WEGpark.park.internal.dto.user.guard;

import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;

public record GetGuardResponseDTO(

        GetParkUserResponseDTO defaults,

        String badgeNumber,

        String location,

        String boss
) {
}

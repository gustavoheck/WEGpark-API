package com.weg.WEGpark.rh.internal.dto.guard;

import java.util.UUID;

public record UpdateGuardResponseDTO(

        UUID uuid,

        String name,

        String telephone,

        String badgeNumber,

        String location,

        String boss
) {
}

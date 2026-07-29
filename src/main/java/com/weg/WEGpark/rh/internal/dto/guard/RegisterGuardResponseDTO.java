package com.weg.WEGpark.rh.internal.dto.guard;

import java.util.UUID;

public record RegisterGuardResponseDTO(
        UUID uuid,

        String email,

        String telephone,

        String name,

        String badgeNumber,

        String location,

        String boss
) {
}

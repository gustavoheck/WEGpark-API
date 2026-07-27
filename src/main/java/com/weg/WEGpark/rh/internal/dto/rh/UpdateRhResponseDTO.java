package com.weg.WEGpark.rh.internal.dto.rh;

import java.util.UUID;

public record UpdateRhResponseDTO(

        UUID uuid,

        String telephone,

        String name,

        String badgeNumber
) {
}

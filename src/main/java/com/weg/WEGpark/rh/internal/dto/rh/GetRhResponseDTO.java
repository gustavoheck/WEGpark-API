package com.weg.WEGpark.rh.internal.dto.rh;

import java.util.UUID;

public record GetRhResponseDTO(

        UUID uuid,

        String email,

        String telephone,

        String name,

        String badgeNumber
) {
}

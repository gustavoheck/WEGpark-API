package com.weg.WEGpark.rh.internal.dto.rh;

public record UpdateRhRequestDTO(
        String telephone,

        String name,

        String badgeNumber
) {
}

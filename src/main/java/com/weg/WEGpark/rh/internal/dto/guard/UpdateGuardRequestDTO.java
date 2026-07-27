package com.weg.WEGpark.rh.internal.dto.guard;

public record UpdateGuardRequestDTO(

        String name,

        String telephone,

        String badgeNumber,

        String location,

        String boss
) {
}

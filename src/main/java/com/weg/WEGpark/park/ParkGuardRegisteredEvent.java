package com.weg.WEGpark.park;

import java.util.UUID;

public record ParkGuardRegisteredEvent(
        Long id,

        UUID uuid,

        String email,

        String telephone,

        String name,

        String badgeNumber,

        String location,

        String boss
) {
}

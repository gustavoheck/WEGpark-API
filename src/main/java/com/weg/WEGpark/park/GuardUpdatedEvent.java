package com.weg.WEGpark.park;

import java.util.UUID;

public record GuardUpdatedEvent(
        Long id,

        UUID uuid,

        String name,

        String telephone,

        String badgeNumber,

        String location,

        String boss
) {
}

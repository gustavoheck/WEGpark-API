package com.weg.WEGpark.rh;

import com.weg.WEGpark.park.GuardUpdatedEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record UpdateGuardEvent(

        CompletableFuture<GuardUpdatedEvent> eventResponse,

        UUID uuid,

        String name,

        String telephone,

        String badgeNumber,

        String location,

        String boss
) {
}

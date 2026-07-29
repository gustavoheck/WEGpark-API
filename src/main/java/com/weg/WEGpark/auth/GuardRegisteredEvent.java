package com.weg.WEGpark.auth;

import com.weg.WEGpark.park.ParkGuardRegisteredEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GuardRegisteredEvent(

        CompletableFuture<ParkGuardRegisteredEvent> registerResponse,

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

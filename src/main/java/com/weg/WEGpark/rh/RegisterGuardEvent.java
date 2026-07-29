package com.weg.WEGpark.rh;

import com.weg.WEGpark.park.ParkGuardRegisteredEvent;

import java.util.concurrent.CompletableFuture;

public record RegisterGuardEvent(
        CompletableFuture<ParkGuardRegisteredEvent> registerResponse,

        String email,

        String password,

        String name,

        String telephone,

        String badgeNumber,

        String location,

        String boss
) {
}

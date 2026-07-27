package com.weg.WEGpark.auth;

import com.weg.WEGpark.park.GuardParkRegisteredEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GuardRegisteredEvent(

        CompletableFuture<GuardParkRegisteredEvent> registerResponse,

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

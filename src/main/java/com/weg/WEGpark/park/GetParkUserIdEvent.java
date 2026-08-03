package com.weg.WEGpark.park;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetParkUserIdEvent(
        CompletableFuture<Long> eventResponse,
        UUID userUuid
) {
}

package com.weg.WEGpark.park;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetParkUserNameEvent(
        CompletableFuture<String> eventResponse,
        UUID userUuid
) {
}

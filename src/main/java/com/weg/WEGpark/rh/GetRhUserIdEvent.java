package com.weg.WEGpark.rh;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetRhUserIdEvent(
        CompletableFuture<Long> eventResponse,
        UUID userUuid
) {
}

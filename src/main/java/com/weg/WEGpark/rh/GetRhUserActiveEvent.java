package com.weg.WEGpark.rh;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetRhUserActiveEvent(
        CompletableFuture<Boolean> eventResponse,
        UUID userUuid
) {
}

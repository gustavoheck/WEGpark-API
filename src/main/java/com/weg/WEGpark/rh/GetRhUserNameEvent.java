package com.weg.WEGpark.rh;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetRhUserNameEvent(
        CompletableFuture<String> eventResponse,
        UUID userUuid
) {
}

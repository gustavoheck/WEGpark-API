package com.weg.WEGpark.rh;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record FindParkUserEvent(
        CompletableFuture<Record> eventResponse,

        UUID userUuid
) {
}

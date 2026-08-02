package com.weg.WEGpark.rh;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record DesactivateAndActivateUserEvent(

        CompletableFuture<Long> userIdResponse,

        UUID uuid
) {
}

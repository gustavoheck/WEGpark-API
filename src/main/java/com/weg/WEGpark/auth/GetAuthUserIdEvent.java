package com.weg.WEGpark.auth;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record GetAuthUserIdEvent(
        CompletableFuture<Long> eventResponse,
        UUID userUuid
) {}

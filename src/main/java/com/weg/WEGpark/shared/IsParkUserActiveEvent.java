package com.weg.WEGpark.shared;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record IsParkUserActiveEvent(

        CompletableFuture<Boolean> eventResponse,

        UUID targetUserUuid
) {
}

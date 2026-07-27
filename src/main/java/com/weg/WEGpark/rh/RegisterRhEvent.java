package com.weg.WEGpark.rh;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;

import java.util.concurrent.CompletableFuture;

public record RegisterRhEvent(

        CompletableFuture<DefaultRegisteredEvent> eventResponse,

        String email,

        String password
) {
}

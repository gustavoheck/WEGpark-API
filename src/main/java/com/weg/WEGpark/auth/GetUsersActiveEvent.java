package com.weg.WEGpark.auth;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public record GetUsersActiveEvent(

        CompletableFuture<Map<Long, Boolean>> eventResponse,

        List<Long> targetUserIds
) {
}

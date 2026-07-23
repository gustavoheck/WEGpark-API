package com.weg.WEGpark.park;

import com.weg.WEGpark.notification.FindAssociationNotificationResponse;

import java.util.concurrent.CompletableFuture;

public record FindAssociationNotificationEvent(

        CompletableFuture<FindAssociationNotificationResponse> eventResponse,

        Long idNotification
) {
}

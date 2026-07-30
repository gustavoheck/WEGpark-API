package com.weg.WEGpark.park;

import java.util.UUID;

public record SendOccurrenceNotificationEvent(

        UUID uuid,

        String nome,

        String email
) {
}

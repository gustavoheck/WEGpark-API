package com.weg.WEGpark.park;

import java.util.UUID;

public record SendOccurrenceNotificationEvent(

        Long idNotificatedUser,

        UUID uuid,

        String nome,

        String email
) {
}

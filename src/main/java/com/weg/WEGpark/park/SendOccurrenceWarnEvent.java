package com.weg.WEGpark.park;

public record SendOccurrenceWarnEvent(

        Long notificatedUserId,

        String email,

        String message
) {
}

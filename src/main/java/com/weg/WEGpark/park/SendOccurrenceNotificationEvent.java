package com.weg.WEGpark.park;

import java.util.List;
import java.util.UUID;

public record SendOccurrenceNotificationEvent(

        List<Long> notificatedUsersId,

        String defaultNotificationMessage,

        UUID occurrenceUuid,

        List<String> userNames,

        List<String> email
) {
}

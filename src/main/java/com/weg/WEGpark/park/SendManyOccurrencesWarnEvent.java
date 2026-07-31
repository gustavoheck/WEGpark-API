package com.weg.WEGpark.park;

import java.util.List;

public record SendManyOccurrencesWarnEvent(

        List<Long> notificatedUsersId,

        List<String> notificatedUsersName,

        List<String> notificatedUsersEmail,

        String defaultNotificationMessage
) {
}

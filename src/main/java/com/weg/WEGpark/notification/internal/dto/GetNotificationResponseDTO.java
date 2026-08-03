package com.weg.WEGpark.notification.internal.dto;

import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetNotificationResponseDTO(
        UUID uuid,
        LocalDateTime notificationTime,
        NotificationType notificationType,
        String message
) {
}

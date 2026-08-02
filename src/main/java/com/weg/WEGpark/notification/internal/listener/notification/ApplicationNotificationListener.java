package com.weg.WEGpark.notification.internal.listener.notification;

import com.weg.WEGpark.notification.internal.app.notification.service.NotificationService;
import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationNotificationListener {

    private final NotificationService notificationService;

    @EventListener
    @Async("asyncTaskExecutor")
    public void sendOcurrenceNotification (SendOccurrenceNotificationEvent event) {
        notificationService.createNewOccurrenceNotification(event);
    }

    @EventListener
    @Async("asyncTaskExecutor")
    public void sendFiveOccurrenceNotification (SendManyOccurrencesWarnEvent event) {
        notificationService.createNewFiveOccurrenceNotification(event);
    }
}

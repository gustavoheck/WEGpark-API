package com.weg.WEGpark.notification.internal.listener.notification;

import com.weg.WEGpark.auth.SendAccountValidationEmailEvent;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationNotificationListener {

    @EventListener
    public void sendOcurrenceNotification (SendOccurrenceNotificationEvent event) {

    }
}

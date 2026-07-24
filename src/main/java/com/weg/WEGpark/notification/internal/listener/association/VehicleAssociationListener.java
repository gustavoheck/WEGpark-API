package com.weg.WEGpark.notification.internal.listener.association;

import com.weg.WEGpark.notification.internal.app.notification.service.NotificationService;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleAssociationListener {

    private final NotificationService notificationService;

    @EventListener
    public void saveAssociationNotification (AssociateToVehicleNotificationEvent event) {
        notificationService.CreateAssociationNotification(event);
    }

    @EventListener
    public void findAssociatioNotification (FindAssociationNotificationEvent event) {
        notificationService.findAssociationNotification(event);
    }
}

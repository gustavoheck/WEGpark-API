package com.weg.WEGpark.notification.internal.listener.association;

import com.weg.WEGpark.notification.internal.app.service.notification.CreateNotificationService;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleAssociationListener {

    private final CreateNotificationService createNotificationService;

    @EventListener
    public void saveAssociationNotification (AssociateToVehicleNotificationEvent event) {
        createNotificationService.CreateAssociationNotification(event);
    }
}

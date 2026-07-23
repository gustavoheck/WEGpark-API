package com.weg.WEGpark.notification.internal.listener.association;

import com.weg.WEGpark.park.AssociateToVehicleEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class VehicleAssociationListener {

    @EventListener
    public void saveAssociationNotification (AssociateToVehicleEvent event) {

    }
}

package com.weg.WEGpark.notification.internal.app.service.mapper;

import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    VehicleAssociationNotification toNotification (AssociateToVehicleNotificationEvent event);
}

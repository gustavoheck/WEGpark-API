package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    AssociateToVehicleNotificationEvent toEvent (ParkUser parkUser, Vehicle vehicle);
}

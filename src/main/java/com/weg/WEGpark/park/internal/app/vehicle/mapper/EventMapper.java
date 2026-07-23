package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "loggedUser.id", target = "idUserToAssociate")
    @Mapping(source = "vehicle.id", target = "idVehicleToAssociate")
    @Mapping(source = "vehicleOwner.id", target = "idNotificatedUser")
    AssociateToVehicleNotificationEvent toEvent (ParkUser loggedUser, Vehicle vehicle, ParkUser vehicleOwner);
}

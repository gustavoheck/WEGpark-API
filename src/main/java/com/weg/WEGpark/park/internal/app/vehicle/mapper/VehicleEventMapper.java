package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleEventMapper {

    @Mapping(source = "loggedUser.id", target = "idUserToAssociate")
    @Mapping(source = "vehicle.id", target = "idVehicleToAssociate")
    @Mapping(source = "vehicleOwner.id", target = "idNotificatedUser")
    @Mapping(source = "loggedUser.name", target = "userToAssociateName")
    @Mapping(source = "vehicle.brand", target = "vehicleBrand")
    @Mapping(source = "vehicle.model", target = "vehicleModel")
    AssociateToVehicleNotificationEvent toEvent (ParkUser loggedUser, Vehicle vehicle, ParkUser vehicleOwner);
}

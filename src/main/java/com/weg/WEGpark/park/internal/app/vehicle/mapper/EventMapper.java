package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.AssociateToVehicleEvent;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {

    AssociateToVehicleEvent toEvent (ParkUser parkUser, Vehicle vehicle);
}

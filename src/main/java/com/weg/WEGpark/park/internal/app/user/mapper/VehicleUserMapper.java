package com.weg.WEGpark.park.internal.app.user.mapper;

import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleUserResponseDTO;
import org.mapstruct.Mapping;

public interface VehicleUserMapper {

    @Mapping(source = "vehicleUser.user.uuid", target = "userUuid")
    GetVehicleUserResponseDTO toResponse (VehicleUser vehicleUser);
}

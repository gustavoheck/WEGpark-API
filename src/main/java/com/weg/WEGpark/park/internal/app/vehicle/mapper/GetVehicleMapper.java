package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetVehicleMapper {

    GetVehicleResponseDTO toResponse (Vehicle vehicle);
}

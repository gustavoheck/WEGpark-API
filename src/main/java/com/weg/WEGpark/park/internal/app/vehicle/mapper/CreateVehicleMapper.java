package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.CreateVehicleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateVehicleMapper {

    Vehicle toEntity (CreateVehicleRequestDTO request);

    CreateVehicleResponseDTO toResponse (Vehicle vehicle);
}

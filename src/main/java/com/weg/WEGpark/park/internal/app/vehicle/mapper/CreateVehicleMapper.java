package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CreateVehicleMapper {

    Vehicle toEntity (CreateVehicleRequestDTO request);

    CreateVehicleResponseDTO toResponse (Vehicle vehicle);
}

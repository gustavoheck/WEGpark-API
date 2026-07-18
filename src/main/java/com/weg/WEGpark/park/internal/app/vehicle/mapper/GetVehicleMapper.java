package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.GetVehicleResponseDTO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface GetVehicleMapper {

    GetVehicleResponseDTO toResponse (Vehicle vehicle);

    Vehicle toEntity (GetVehicleResponseDTO request);
}

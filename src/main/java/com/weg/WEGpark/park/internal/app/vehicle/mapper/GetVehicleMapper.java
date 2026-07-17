package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.GetVehicleResponseDTO;

public class GetVehicleMapper {

    public GetVehicleResponseDTO toResponse (Vehicle vehicle) {
        return new GetVehicleResponseDTO(
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getBrand(),
                vehicle.getColor()
        );
    }
}

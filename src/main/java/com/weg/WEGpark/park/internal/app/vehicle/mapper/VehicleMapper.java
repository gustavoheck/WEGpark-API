package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.CreateVehicleResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public Vehicle toEntity (CreateVehicleRequestDTO request) {
        return new Vehicle(
                request.plate(),
                request.model(),
                request.brand(),
                request.color()
        );
    }

    public CreateVehicleResponseDTO toResponse (Vehicle vehicle) {
        return new CreateVehicleResponseDTO(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getModel(),
                vehicle.getBrand(),
                vehicle.getColor()
        );
    }
}

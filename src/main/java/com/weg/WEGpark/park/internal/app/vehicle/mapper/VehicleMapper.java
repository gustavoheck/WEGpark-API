package com.weg.WEGpark.park.internal.app.vehicle.mapper;

import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity (CreateVehicleRequestDTO request);

    @Mapping(source = "vehicleUsers", target = "vehicleUsers")
    @Mapping(source = "vehicle", target = ".")
    GetVehicleResponseDTO toGetResponse (Vehicle vehicle, List<GetVehicleUserResponseDTO> vehicleUsers);

    UpdateVehicleResponseDTO toUpdateResponse (Vehicle vehicle);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateVehicleRequestDTO dto, @MappingTarget Vehicle vehicle);
}

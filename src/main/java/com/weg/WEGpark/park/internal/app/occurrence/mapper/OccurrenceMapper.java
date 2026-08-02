package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.guard.GetGuardResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    Occurrence toEntity(CreateOccurrenceRequestDTO occurrenceRequestDto);

    @Mapping(source = "guardResponse", target = "guard")
    @Mapping(source = "vehicleResponse", target = "vehicle")
    @Mapping(source = "occurrence.location", target = "location")
    DefaultOccurrenceResponseDTO toDefaultOccurrenceDTO
            (Occurrence occurrence, GetVehicleResponseDTO vehicleResponse, GetGuardResponseDTO guardResponse);
}

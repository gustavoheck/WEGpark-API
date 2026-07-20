package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrafficAccidentMapper {

        @Mapping(source = "defaults", target = ".")
        TrafficAccident toEntity(CreateTrafficAccidentRequestDTO request);

        @Mapping(source = ".", target = "defaults")
        CreateTrafficAccidentResponseDTO toResponse(TrafficAccident trafficAccident);
}

package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.UpdateWarningRequestDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarningMapper {

    @Mapping(source = "defaults", target = ".")
    Warning toEntity(CreateWarningRequestDTO request);

    @Mapping(source = ".", target = "defaults")
    CreateWarningResponseDTO toCreateResponse(Warning warning);

    @Mapping(source = ".", target = "defaults")
    GetWarningResponseDTO toGetResponse(Warning warning);

    @Mapping(source = "defaults", target = ".")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateWarningRequestDTO dto, @MappingTarget Warning warning);
}

package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.UpdateWarningRequestDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WarningMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(source = "request.defaults", target = ".")
    @Mapping(source = "request", target = ".")
    @Mapping(source = "guard", target = "guard")
    Warning toEntity(CreateWarningRequestDTO request, Guard guard);

    @Mapping(source = "warning", target = ".")
    @Mapping(source = "occurrenceDefaults", target = "defaults")
    CreateWarningResponseDTO toCreateResponse(Warning warning, DefaultOccurrenceResponseDTO occurrenceDefaults);

    @Mapping(source = ".", target = "defaults")
    GetWarningResponseDTO toGetResponse(Warning warning);

    @Mapping(source = "defaults", target = ".")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateWarningRequestDTO dto, @MappingTarget Warning warning);
}

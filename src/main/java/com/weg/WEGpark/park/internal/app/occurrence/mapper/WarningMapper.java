package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarningMapper {

    @Mapping(source = "defaults", target = ".")
    Warning toEntity(CreateWarningRequestDTO request);

    @Mapping(source = ".", target = "defaults")
    CreateWarningResponseDTO toCreateResponse(Warning warning);

    @Mapping(source = ".", target = "defaults")
    GetWarningResponseDTO toGetResponse(Warning warning);
}

package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    Occurrence toEntity(CreateOccurrenceRequestDTO occurrenceRequestDto);
}

package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    Occurrence toEntity(CreateOccurrenceRequestDto occurrenceRequestDto);

    CreateOccurrenceResponseDto toResponse(Occurrence occurrence);
}

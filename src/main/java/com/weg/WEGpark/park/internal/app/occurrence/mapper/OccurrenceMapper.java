package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    public Occurrence toEntity(OccurrenceRequestDto occurrenceRequestDto);

    public OccurrenceResponseDto toResponse(Occurrence occurrence);
}

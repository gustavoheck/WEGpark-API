package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceResponseDto;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
public class OccurrenceMapper {

    public Occurrence toEntity(OccurrenceRequestDto occurrenceRequestDto) {
        return new Occurrence(
                occurrenceRequestDto.location(),
                occurrenceRequestDto.gate()
        );
    }

    public OccurrenceResponseDto toResponse(Occurrence occurrence) {
        return new OccurrenceResponseDto(
                occurrence.getId(),
                occurrence.getDateHour(),
                occurrence.getLocation(),
                occurrence.getGate()
        );
    }
}

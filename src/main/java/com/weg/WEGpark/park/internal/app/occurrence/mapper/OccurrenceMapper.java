package com.weg.WEGpark.park.internal.app.occurrence.mapper;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OccurrenceMapper {

    Occurrence toEntity(CreateOccurrenceRequestDto occurrenceRequestDto);
}

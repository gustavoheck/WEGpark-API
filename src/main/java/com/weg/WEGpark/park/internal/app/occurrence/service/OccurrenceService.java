package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceMapper;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.OccurrenceResponseDto;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final OccurrenceMapper occurrenceMapper;

    @Transactional
    public OccurrenceResponseDto registerOccurrence(OccurrenceRequestDto occurrenceRequestDto) {
        Occurrence occurrence = occurrenceMapper.toEntity(occurrenceRequestDto);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrence = occurrenceRepository.save(occurrence);

        return occurrenceMapper.toResponse(occurrence);
    }



}

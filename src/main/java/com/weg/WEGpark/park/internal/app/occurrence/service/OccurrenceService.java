package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceMapper;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceRequestDto;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.OccurrenceResponseDto;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

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

    public List<OccurrenceResponseDto> findAllOccurrences() {

        List<Occurrence> occurrences = occurrenceRepository.findAll();

        return occurrences.stream()
                .map(occurrenceMapper::toResponse)
                .toList();
    }

    public List<OccurrenceResponseDto> findByDate(YearMonth period) {

        LocalDateTime startMonth = period.atDay(1).atStartOfDay();

        LocalDateTime endMonth = period.atEndOfMonth().atTime(LocalTime.MAX);

        List<Occurrence> occurrences = occurrenceRepository.findByDateHourBetween(startMonth, endMonth);

        return occurrences.stream()
                .map(occurrenceMapper::toResponse)
                .toList();
    }

    public List<OccurrenceResponseDto> findByLocal(String location) {

        List<Occurrence> occurrences = occurrenceRepository.findByLocation(location);

        return occurrences.stream()
                .map(occurrenceMapper::toResponse)
                .toList();
    }

}

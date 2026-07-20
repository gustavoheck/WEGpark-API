package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrafficAccidentService {

    private final OccurrenceRepository occurrenceRepository;

    private final TrafficAccidentMapper trafficAccidentMapper;

    @Transactional
    public CreateTrafficAccidentResponseDTO registerTrafficAccidentOccurrence (CreateTrafficAccidentRequestDTO request) {
        TrafficAccident occurrence = trafficAccidentMapper.toEntity(request);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrence = occurrenceRepository.save(occurrence);

        return trafficAccidentMapper.toResponse(occurrence);
    }
}

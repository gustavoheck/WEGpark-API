package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.app.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
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
        occurrence.setOccurrenceType(OccurrenceType.TRAFFIC_ACCIDENT);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrenceRepository.save(occurrence);

        return trafficAccidentMapper.toCreateResponse(occurrence);
    }

    @Transactional
    public GetTrafficAccidentResponseDTO updateTrafficAccident(Long id, UpdateTrafficAccidentRequestDTO request) {
        TrafficAccident occurrence = (TrafficAccident) occurrenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Any occurrence of the traffic accident type was found by %s ".formatted(id)));

        trafficAccidentMapper.updateFromDto(request, occurrence);

        occurrenceRepository.save(occurrence);

        return trafficAccidentMapper.toGetResponse(occurrence);
    }
}

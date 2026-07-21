package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllegalParkingService {

    private final IllegalParkingMapper illegalParkingMapper;

    private final OccurrenceRepository occurrenceRepository;

    @Transactional
    public CreateIllegalParkingResponseDTO registerIllegalParkingOccurrence (CreateIllegalParkingRequestDTO request) {

        IllegalParking occurrence = illegalParkingMapper.toEntity(request);
        occurrence.setOccurrenceType(OccurrenceType.ILLEGAL_PARKING);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrenceRepository.save(occurrence);

        return illegalParkingMapper.toCreateResponse(occurrence);
    }
}

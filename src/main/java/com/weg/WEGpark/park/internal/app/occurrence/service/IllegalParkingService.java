package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Transactional
    public GetIllegalParkingResponseDTO updateIllegalParking(UUID uuid, UpdateIllegalParkingRequestDTO request) {
        IllegalParking occurrence = (IllegalParking) occurrenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any occurrence of the illegal parking type was found by %s ".formatted(uuid)));

        illegalParkingMapper.updateFromDto(request, occurrence);

        occurrenceRepository.save(occurrence);

        return illegalParkingMapper.toGetResponse(occurrence);
    }
}

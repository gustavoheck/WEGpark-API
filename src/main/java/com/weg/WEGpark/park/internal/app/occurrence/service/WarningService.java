package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarningService {

    private final OccurrenceRepository occurrenceRepository;

    private final WarningMapper warningMapper;

    @Transactional
    public CreateWarningResponseDTO registerWarningOccurrence (CreateWarningRequestDTO request) {

        Warning occurrence = warningMapper.toEntity(request);
        occurrence.setOccurrenceType(OccurrenceType.WARNING);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrenceRepository.save(occurrence);

        return warningMapper.toResponse(occurrence);
    }
}

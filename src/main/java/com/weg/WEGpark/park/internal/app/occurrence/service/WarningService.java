package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.UpdateWarningRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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

        return warningMapper.toCreateResponse(occurrence);
    }

    @Transactional
    public GetWarningResponseDTO updateWarning (UUID uuid, UpdateWarningRequestDTO request) {
        Warning occurrence = (Warning) occurrenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any occurrence of the warning type was found by %s ".formatted(uuid)));

        warningMapper.updateFromDto(request, occurrence);

        occurrenceRepository.save(occurrence);

        return warningMapper.toGetResponse(occurrence);
    }
}

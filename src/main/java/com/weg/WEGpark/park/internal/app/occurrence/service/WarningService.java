package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.CreateWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.UpdateWarningRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarningService {

    private final OccurrenceRepository occurrenceRepository;
    private final OccurrenceService occurrenceService;

    private final WarningMapper warningMapper;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OccurrenceNotificationMapper occurrenceNotificationMapper;

    @Transactional
    public CreateWarningResponseDTO registerWarningOccurrence (
            CreateWarningRequestDTO request,
            JWTUserData jwtUserData
    ) {

        RegisterDefaultInfo info = occurrenceService.findRegisterBasics(request.defaults().plate(), jwtUserData);

        Warning occurrence = warningMapper.toEntity(request, info);
        occurrence.setOccurrenceType(OccurrenceType.WARNING);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrenceRepository.saveAndFlush(occurrence);

        Vehicle vehicle = occurrence.getVehicleUsers().getFirst().getVehicle();
        applicationEventPublisher.publishEvent(occurrenceNotificationMapper.toNotification(
                occurrence,
                """
                        Uma nova ocorrencia foi registrada para o seu veículo %s da placa %s,
                        este veículo acabou recebendo um aviso, confira mais acessando a ocorrência!
                """.formatted("%s %s".formatted(vehicle.getBrand(), vehicle.getModel()), vehicle.getPlate())
        ));

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

package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
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
public class TrafficAccidentService {

    private final OccurrenceRepository occurrenceRepository;
    private final OccurrenceService occurrenceService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final TrafficAccidentMapper trafficAccidentMapper;

    private final OccurrenceNotificationMapper occurrenceNotificationMapper;

    @Transactional
    public CreateTrafficAccidentResponseDTO registerTrafficAccidentOccurrence (
            CreateTrafficAccidentRequestDTO request,
            JWTUserData jwtUserData
    ) {

        RegisterDefaultInfo info = occurrenceService.findRegisterBasics(request.defaults().plate(), jwtUserData);

        TrafficAccident occurrence = trafficAccidentMapper.toEntity(request, info);
        occurrence.setOccurrenceType(OccurrenceType.TRAFFIC_ACCIDENT);

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        occurrenceRepository.saveAndFlush(occurrence);

        Vehicle vehicle = occurrence.getVehicleUsers().getFirst().getVehicle();
        applicationEventPublisher.publishEvent(occurrenceNotificationMapper.toNotification(
                occurrence,
                """
                        Uma nova ocorrencia foi registrada para o seu veículo %s da placa %s,
                        este veículo acabou sofrendo um sinistro de transito,
                        confira mais acessando a ocorrência!
                """.formatted("%s %s".formatted(vehicle.getBrand(), vehicle.getModel()), vehicle.getPlate())
        ));
        occurrence.getVehicleUsers()
                .forEach(vehicleUser ->
                        occurrenceService.checkAndSendFiveOccurrenceWarn(vehicleUser.getParkUser().getId(), occurrence));

        return trafficAccidentMapper.toCreateResponse(occurrence);
    }

    @Transactional
    public GetTrafficAccidentResponseDTO updateTrafficAccident(UUID uuid, UpdateTrafficAccidentRequestDTO request) {
        TrafficAccident occurrence = (TrafficAccident) occurrenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any occurrence of the traffic accident type was found by %s ".formatted(uuid)));

        trafficAccidentMapper.updateFromDto(request, occurrence);

        occurrenceRepository.save(occurrence);

        return trafficAccidentMapper.toGetResponse(occurrence);
    }
}

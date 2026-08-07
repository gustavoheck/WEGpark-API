package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.CreateTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.GetTrafficAccidentResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.UpdateTrafficAccidentRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.TrafficAccidentRepository;
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

    private final TrafficAccidentRepository trafficAccidentRepository;
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

        TrafficAccident occurrence = trafficAccidentMapper.toEntity(request, info.guard());

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        info.vehicleUsers().forEach(vu -> occurrence.getVehicleUsers().add(vu));

        TrafficAccident savedOccurrence = trafficAccidentRepository.saveAndFlush(occurrence);

        occurrenceService.fiveOccurrenceWarn(info.vehicleUsers());

        Vehicle vehicle = info.vehicleUsers().getFirst().getVehicle();
        applicationEventPublisher.publishEvent(occurrenceNotificationMapper.toNotification(
                info.vehicleUsers(),
                occurrence,
                """
                        Uma nova ocorrência foi registrada para o seu veículo %s da placa %s,
                        este veículo acabou sofrendo um sinistro de trânsito,
                        confira mais acessando a ocorrência!
                """.formatted("%s %s".formatted(vehicle.getBrand(), vehicle.getModel()), vehicle.getPlate())
        ));

        return trafficAccidentMapper.toCreateResponse
                (occurrence, occurrenceService.getOccurrenceResponse(savedOccurrence, vehicle));
    }

    @Transactional
    public GetTrafficAccidentResponseDTO updateTrafficAccident(UUID uuid, UpdateTrafficAccidentRequestDTO request) {
        TrafficAccident occurrence = trafficAccidentRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Any occurrence of the traffic accident type was found by %s ".formatted(uuid)));

        trafficAccidentMapper.updateFromDto(request, occurrence);

        trafficAccidentRepository.save(occurrence);

        Vehicle vehicle = occurrence.getVehicleUsers().getFirst().getVehicle();

        return trafficAccidentMapper.toGetResponse(occurrence, occurrenceService.getOccurrenceResponse(occurrence, vehicle));
    }
}

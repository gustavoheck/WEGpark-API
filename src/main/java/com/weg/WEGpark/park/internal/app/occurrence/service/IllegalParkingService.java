package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.CreateIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.GetIllegalParkingResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.UpdateIllegalParkingRequestDTO;
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
public class IllegalParkingService {

    private final IllegalParkingMapper illegalParkingMapper;
    private final OccurrenceNotificationMapper occurrenceNotificationMapper;

    private final OccurrenceService occurrenceService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final OccurrenceRepository occurrenceRepository;

    @Transactional
    public CreateIllegalParkingResponseDTO registerIllegalParkingOccurrence (
            CreateIllegalParkingRequestDTO request,
            JWTUserData jwtUserData) {

        RegisterDefaultInfo info = occurrenceService.findRegisterBasics(request.defaults().plate(), jwtUserData);

        IllegalParking occurrence = illegalParkingMapper.toEntity(request, info.guard());

        LocalDateTime date = LocalDateTime.now();
        occurrence.setDateHour(date);

        info.vehicleUsers().forEach(vu -> occurrence.getVehicleUsers().add(vu));

        IllegalParking savedOccurrence = occurrenceRepository.saveAndFlush(occurrence);

        occurrenceService.fiveOccurrenceWarn(info.vehicleUsers());

        Vehicle vehicle = info.vehicleUsers().getFirst().getVehicle();
        applicationEventPublisher.publishEvent(occurrenceNotificationMapper.toNotification(
                info.vehicleUsers(),
                occurrence,
                """
                        Uma nova ocorrencia foi registrada para o seu veículo %s da placa %s, seu veículo foi
                        encontrado estacionado em um local não permitido,
                        confira mais acessando a ocorrência!
                """.formatted("%s %s".formatted(vehicle.getBrand(), vehicle.getModel()), vehicle.getPlate())
        ));

        return illegalParkingMapper.toCreateResponse
                (occurrence, occurrenceService.getOccurrenceResponse(savedOccurrence, vehicle));
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

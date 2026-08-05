package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.SendOccurrenceWarnEvent;
import com.weg.WEGpark.park.SendOccurrenceWarnToGuardEvent;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.*;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.guard.GetGuardResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.GetVehicleResponseDTO;
import com.weg.WEGpark.shared.util.FilterUtil;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.*;
import com.weg.WEGpark.park.internal.infra.specification.OccurrenceSpecification;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;
    private final VehicleRepository vehicleRepository;
    private final GuardRepository guardRepository;

    private final IllegalParkingMapper illegalParkingMapper;
    private final TrafficAccidentMapper trafficAccidentMapper;
    private final VehicleUserMapper vehicleUserMapper;
    private final OccurrenceMapper occurrenceMapper;
    private final WarningMapper warningMapper;
    private final GuardMapper guardMapper;
    private final VehicleMapper vehicleMapper;

    private final OccurrenceNotificationMapper occurrenceNotificationMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Page<Record> findAllOccurrences(FilterOccurrenceRequestDTO filter, Pageable pageable) {

        if (FilterUtil.checkMoreThanOneFilter(filter)) {
            Specification<Occurrence> spec = Specification
                    .where(OccurrenceSpecification.hasLocal(filter.location()))
                    .and(OccurrenceSpecification.hasGate(filter.gate()))
                    .and(OccurrenceSpecification.hasDate(filter.yearMonth()))
                    .and(OccurrenceSpecification.hasType(filter.occurrenceType() == null ? null : filter.occurrenceType().toString()))
                    .and(OccurrenceSpecification.hasRecents(filter.recents()))
                    .and(OccurrenceSpecification.hasPlate(filter.plate()))
                    .and(OccurrenceSpecification.hasResponsibleName(filter.responsableName()))
                    .and(OccurrenceSpecification.hasBadgeNumber(filter.badgeNumber()));

            Page<Occurrence> occurrencePage = occurrenceRepository.findAll(spec, pageable);

            return occurrencePage.map(this::toResponse);
        }
        throw new MoreThenOneFilterException("You can't use more than one filter");
    }

    public Page<Record> findMyOccurrences(JWTUserData jwtUserData, Pageable pageable) {
        return occurrenceRepository.findAllByParkUserUuid(jwtUserData.uuid(), pageable)
                .map(this::toResponse);
    }

    public Record findOccurrenceByUuid(UUID uuid) {
        Occurrence occurrence = occurrenceRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException(
                        "Any occurrence was found by %s uuid".formatted(uuid)
                ));

        return toResponse(occurrence);
    }

    public RegisterDefaultInfo findRegisterBasics (String plate, JWTUserData jwtUserData) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new NotFoundException("Any vehicle was found by %s plate".formatted(plate)));
        Guard guard = guardRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any guard was found by the logged uuid"));

        return new RegisterDefaultInfo(
            vehicle.getParkUsers(),
            guard
        );
    }

    public void fiveOccurrenceWarn (List<VehicleUser> occurrenceVehicleUsers) {
        List<VehicleUser> usersToNotificate = new ArrayList<>();
        occurrenceVehicleUsers.forEach(vehicleUser -> {
            if (checkFiveOccurrenceWarn(vehicleUser.getParkUser().getId())) {
                usersToNotificate.add(vehicleUser);
            }
        });

        applicationEventPublisher.publishEvent (
                occurrenceNotificationMapper.toFiveOccurrenceNotification(
                        usersToNotificate,
                        """
                                Identificamos 5 ou mais registros de ocorrências/avisos no seu nome nos últimos 30 dias.
                                Solicitamos que consulte o seu histórico e
                                evite novas infrações que possam gerar penalidades à você.
                        """
                )
        );
    }

    public DefaultOccurrenceResponseDTO getOccurrenceResponse (Occurrence occurrence, Vehicle vehicle) {
        GetGuardResponseDTO guardResponse = guardMapper.toGetResponse(occurrence.getGuard(), true);
        GetVehicleResponseDTO vehicleResponse = vehicleMapper.toGetResponse(vehicle,
                vehicle
                        .getParkUsers()
                        .stream()
                        .map(vehicleUserMapper::toResponse)
                        .toList()
        );

        return occurrenceMapper.toDefaultOccurrenceDTO(occurrence, vehicleResponse, guardResponse);
    }

    private Boolean checkFiveOccurrenceWarn (Long parkUserId) {
        Integer qtdLastOccurrences =
                occurrenceRepository.countHowManyOccurrencesLastDays(parkUserId, LocalDateTime.now().minusDays(30));
        System.out.println(qtdLastOccurrences);
        if (qtdLastOccurrences >= 5) {
            return true;
        }
        return false;
    }

    private Record toResponse(Occurrence occurrence) {
        return switch (occurrence) {
            case Warning warning -> warningMapper.toGetResponse(warning);
            case IllegalParking illegalParking -> illegalParkingMapper.toGetResponse(illegalParking);
            case TrafficAccident trafficAccident -> trafficAccidentMapper.toGetResponse(trafficAccident);
            default -> throw new IllegalStateException("Unexpected value: " + occurrence);
        };
    }
}

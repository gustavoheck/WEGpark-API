package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccurrenceService {

    private final OccurrenceRepository occurrenceRepository;

    private final IllegalParkingMapper illegalParkingMapper;
    private final VehicleRepository vehicleRepository;
    private final GuardRepository guardRepository;
    private final TrafficAccidentMapper trafficAccidentMapper;
    private final WarningMapper warningMapper;

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

            Page<Record> occurrenceResponsePage  = occurrencePage.map(occurrence -> {
                switch (occurrence) {
                    case Warning warning -> {
                        return warningMapper.toGetResponse(warning);
                    }
                    case IllegalParking illegalParking -> {
                        return illegalParkingMapper.toGetResponse(illegalParking);
                    }
                    case TrafficAccident trafficAccident -> {
                        return trafficAccidentMapper.toGetResponse(trafficAccident);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + occurrence);
                }
            });
            return occurrenceResponsePage;
        }
        throw new MoreThenOneFilterException("You can't use more than one filter");
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

    public void checkAndSendFiveOccurrenceWarn (Long parkUserId, Occurrence occurrence) {
        Integer qtdLastOccurrences =
                occurrenceRepository.countHowManyOccurrencesLastDays(parkUserId, LocalDateTime.now().minusDays(30));

        if (qtdLastOccurrences >= 5) {
            List<Guard> guardList = guardRepository.findAll();

            List<Long> allGuardId = guardList
                    .stream()
                    .map(ParkUser::getId)
                    .toList();

            List<String> allGuardEmail = guardList
                    .stream()
                    .map(ParkUser::getEmail)
                    .toList();

            applicationEventPublisher.publishEvent
                    (occurrenceNotificationMapper.toFiveOccurrenceNotification(
                            occurrence,
                            """
                                    Identificamos %s registros de ocorrências/avisos no seu nome nos últimos 30 dias.
                                    Solicitamos que acesse a plataforma WEGpark para consultar o seu histórico e
                                    evitar novas infrações que possam gerar penalidades à você ou restrições a sua conta.
                            """.formatted(qtdLastOccurrences)
                    ));
        }
    }
}

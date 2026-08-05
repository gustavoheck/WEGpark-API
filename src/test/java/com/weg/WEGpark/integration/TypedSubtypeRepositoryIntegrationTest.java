package com.weg.WEGpark.integration;

import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.GuardRepository;
import com.weg.WEGpark.park.internal.infra.repository.IllegalParkingRepository;
import com.weg.WEGpark.park.internal.infra.repository.TrafficAccidentRepository;
import com.weg.WEGpark.park.internal.infra.repository.WarningRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TypedSubtypeRepositoryIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired private CollaboratorRepository collaboratorRepository;
    @Autowired private GuardRepository guardRepository;
    @Autowired private WarningRepository warningRepository;
    @Autowired private TrafficAccidentRepository trafficAccidentRepository;
    @Autowired private IllegalParkingRepository illegalParkingRepository;
    @Autowired private EntityManager entityManager;

    @Test
    void repositoriesOnlyReturnTheirOwnEntitySubtype() {
        Guard guard = createGuard();
        Collaborator collaborator = createCollaborator();

        Warning warning = new Warning("Gate A", "Factory A", OccurrenceType.WARNING,
                WarningType.OTHER, "Warning");
        warning.setDateHour(LocalDateTime.now());
        warning.setGuard(guard);
        warning = warningRepository.saveAndFlush(warning);

        TrafficAccident trafficAccident = new TrafficAccident(
                "Gate B", "Factory B", OccurrenceType.TRAFFIC_ACCIDENT, LocalDateTime.now(),
                "Victim", "Boss", "Factory B", "Section", "Collision", "Guard", "Victim"
        );
        trafficAccident.setDateHour(LocalDateTime.now());
        trafficAccident.setGuard(guard);
        trafficAccident = trafficAccidentRepository.saveAndFlush(trafficAccident);

        IllegalParking illegalParking = new IllegalParking(
                "Gate C", "Factory C", OccurrenceType.ILLEGAL_PARKING,
                ParkingSpaceType.COMMON, "Illegal parking"
        );
        illegalParking.setDateHour(LocalDateTime.now());
        illegalParking.setGuard(guard);
        illegalParking = illegalParkingRepository.saveAndFlush(illegalParking);

        UUID warningUuid = warning.getUuid();
        UUID trafficAccidentUuid = trafficAccident.getUuid();
        UUID illegalParkingUuid = illegalParking.getUuid();
        UUID collaboratorUuid = collaborator.getUuid();
        entityManager.clear();

        assertAll(
                () -> assertTrue(warningRepository.findByUuid(warningUuid).isPresent()),
                () -> assertTrue(trafficAccidentRepository.findByUuid(warningUuid).isEmpty()),
                () -> assertTrue(illegalParkingRepository.findByUuid(warningUuid).isEmpty()),
                () -> assertTrue(trafficAccidentRepository.findByUuid(trafficAccidentUuid).isPresent()),
                () -> assertTrue(warningRepository.findByUuid(trafficAccidentUuid).isEmpty()),
                () -> assertTrue(illegalParkingRepository.findByUuid(illegalParkingUuid).isPresent()),
                () -> assertTrue(warningRepository.findByUuid(illegalParkingUuid).isEmpty()),
                () -> assertTrue(guardRepository.findByUuid(guard.getUuid()).isPresent()),
                () -> assertTrue(guardRepository.findByUuid(collaboratorUuid).isEmpty())
        );
    }

    private Guard createGuard() {
        return guardRepository.saveAndFlush(new Guard(
                900001L, UUID.randomUUID(), "typed-guard@weg.net", "11999999991",
                "Typed Guard", "TG-01", "Gate A", "Security"
        ));
    }

    private Collaborator createCollaborator() {
        return collaboratorRepository.saveAndFlush(new Collaborator(
                900002L, UUID.randomUUID(), "typed-collaborator@weg.net", "11999999992",
                "Typed Collaborator", "TC-01", "Factory A"
        ));
    }
}

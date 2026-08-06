package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.park.SendManyOccurrencesWarnEvent;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.*;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.filter.FilterOccurrenceRequestDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.GetWarningResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.*;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OccurrenceServiceTest {
    private OccurrenceRepository occurrenceRepository;
    private VehicleRepository vehicleRepository;
    private GuardRepository guardRepository;
    private OccurrenceMapper occurrenceMapper;
    private WarningMapper warningMapper;
    private GuardMapper guardMapper;
    private VehicleMapper vehicleMapper;
    private OccurrenceNotificationMapper notificationMapper;
    private ApplicationEventPublisher publisher;
    private OccurrenceService service;

    @BeforeEach
    void setUp() {
        occurrenceRepository = mock(OccurrenceRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        guardRepository = mock(GuardRepository.class);
        occurrenceMapper = mock(OccurrenceMapper.class);
        warningMapper = mock(WarningMapper.class);
        guardMapper = mock(GuardMapper.class);
        vehicleMapper = mock(VehicleMapper.class);
        notificationMapper = mock(OccurrenceNotificationMapper.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new OccurrenceService(occurrenceRepository, vehicleRepository, guardRepository, mock(IllegalParkingMapper.class), mock(TrafficAccidentMapper.class), mock(VehicleUserMapper.class), occurrenceMapper, warningMapper, guardMapper, vehicleMapper, notificationMapper, publisher);
    }

    @Test
    void findsEmptyOccurrencePageForValidFilter() {
        FilterOccurrenceRequestDTO filter = new FilterOccurrenceRequestDTO(null, null, null, null, null, null, null, null);
        var pageable = PageRequest.of(0, 10);
        when(occurrenceRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable))).thenReturn(org.springframework.data.domain.Page.empty(pageable));

        assertTrue(service.findAllOccurrences(filter, pageable).isEmpty());
    }

    @Test
    void findsOnlyOccurrencesAssociatedWithLoggedUser() {
        UUID userUuid = UUID.randomUUID();
        JWTUserData parkUser = new JWTUserData(userUuid, "park@weg.net", List.of("ROLE_PARK"), "Park User");
        var pageable = PageRequest.of(0, 10);
        Warning warning = mock(Warning.class);
        GetWarningResponseDTO response = new GetWarningResponseDTO(null, null, null, null);
        WarningMapper warningMapper = mock(WarningMapper.class);
        service = new OccurrenceService(
                occurrenceRepository, vehicleRepository, guardRepository,
                mock(IllegalParkingMapper.class), mock(TrafficAccidentMapper.class), mock(VehicleUserMapper.class),
                occurrenceMapper, warningMapper, guardMapper, vehicleMapper, notificationMapper, publisher
        );
        when(occurrenceRepository.findAllByParkUserUuid(userUuid, pageable))
                .thenReturn(new PageImpl<>(List.of(warning), pageable, 1));
        when(warningMapper.toGetResponse(warning)).thenReturn(response);

        Page<Record> result = service.findMyOccurrences(parkUser, pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(response, result.getContent().getFirst());
        verify(occurrenceRepository).findAllByParkUserUuid(userUuid, pageable);
    }

    @Test
    void findsOccurrenceByUuidAndMapsItsConcreteType() {
        UUID occurrenceUuid = UUID.randomUUID();
        Warning warning = mock(Warning.class);
        GetWarningResponseDTO response = new GetWarningResponseDTO(null, null, null, null);
        when(occurrenceRepository.findByUuid(occurrenceUuid)).thenReturn(Optional.of(warning));
        when(warningMapper.toGetResponse(warning)).thenReturn(response);

        assertSame(response, service.findOccurrenceByUuid(occurrenceUuid));
        verify(occurrenceRepository).findByUuid(occurrenceUuid);
    }

    @Test
    void rejectsMissingOccurrenceUuid() {
        UUID occurrenceUuid = UUID.randomUUID();
        when(occurrenceRepository.findByUuid(occurrenceUuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.findOccurrenceByUuid(occurrenceUuid));
    }

    @Test
    void loadsVehicleAndLoggedGuardForOccurrenceRegistration() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        Guard guard = new Guard(2L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "2", "A", "Boss");
        UUID guardUuid = guard.getUuid();
        when(vehicleRepository.findByPlate("ABC1234")).thenReturn(Optional.of(vehicle));
        when(guardRepository.findByUuid(guardUuid)).thenReturn(Optional.of(guard));

        RegisterDefaultInfo info = service.findRegisterBasics("ABC1234", new JWTUserData(guardUuid, "g@weg.net", List.of("ROLE_GUARD"), "Guard"));

        assertSame(guard, info.guard());
        assertSame(vehicle.getParkUsers(), info.vehicleUsers());
    }

    @Test
    void rejectsMissingVehicleOrGuardForOccurrenceRegistration() {
        UUID guardUuid = UUID.randomUUID();
        when(vehicleRepository.findByPlate("missing")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findRegisterBasics("missing", new JWTUserData(guardUuid, "g@weg.net", List.of(), "Guard")));
    }

    @Test
    void publishesFiveOccurrenceWarningOnlyForUsersAtThreshold() {
        ParkUser user = new ParkUser(1L, UUID.randomUUID(), "u@weg.net", "1", "User");
        VehicleUser association = new VehicleUser(user, new Vehicle("ABC1234", "M", "B", "C"));
        when(occurrenceRepository.countHowManyOccurrencesLastDays(eq(1L), any())).thenReturn(5);
        when(notificationMapper.toFiveOccurrenceNotification(anyList(), anyString()))
                .thenReturn(new SendManyOccurrencesWarnEvent(List.of(1L), List.of("User"), List.of("u@weg.net"), "message"));

        service.fiveOccurrenceWarn(List.of(association));

        verify(notificationMapper).toFiveOccurrenceNotification(eq(List.of(association)), anyString());
        verify(publisher).publishEvent(any(Object.class));
    }

    @Test
    void mapsDefaultOccurrenceResponse() {
        Occurrence occurrence = mock(Occurrence.class);
        Vehicle vehicle = new Vehicle("ABC1234", "M", "B", "C");
        var response = mock(DefaultOccurrenceResponseDTO.class);
        when(occurrence.getGuard()).thenReturn(new Guard(1L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "2", "A", "Boss"));
        when(occurrenceMapper.toDefaultOccurrenceDTO(eq(occurrence), any(), any())).thenReturn(response);

        assertSame(response, service.getOccurrenceResponse(occurrence, vehicle));
    }
}

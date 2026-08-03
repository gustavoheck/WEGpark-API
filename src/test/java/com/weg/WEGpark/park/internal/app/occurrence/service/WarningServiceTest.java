package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.WarningMapper;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.WarningType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Warning;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.warning.*;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WarningServiceTest {
    private OccurrenceRepository repository;
    private OccurrenceService occurrenceService;
    private WarningMapper mapper;
    private ApplicationEventPublisher publisher;
    private OccurrenceNotificationMapper notificationMapper;
    private WarningService service;
    private Warning warning;

    @BeforeEach
    void setUp() {
        repository = mock(OccurrenceRepository.class);
        occurrenceService = mock(OccurrenceService.class);
        mapper = mock(WarningMapper.class);
        publisher = mock(ApplicationEventPublisher.class);
        notificationMapper = mock(OccurrenceNotificationMapper.class);
        service = new WarningService(repository, occurrenceService, mapper, publisher, notificationMapper);
        warning = new Warning("gate", "local", OccurrenceType.WARNING, WarningType.OTHER, "description");
    }

    @Test
    void registersWarningWithVehicleUsersAndNotifications() {
        CreateWarningRequestDTO request = mock(CreateWarningRequestDTO.class);
        Vehicle vehicle = new Vehicle("ABC1234", "M", "B", "C");
        VehicleUser user = new VehicleUser(new ParkUser(1L, UUID.randomUUID(), "u@weg.net", "1", "User"), vehicle);
        Guard guard = new Guard(2L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "2", "A", "Boss");
        when(request.defaults()).thenReturn(mock(com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO.class));
        when(request.defaults().plate()).thenReturn("ABC1234");
        when(occurrenceService.findRegisterBasics(eq("ABC1234"), any())).thenReturn(new RegisterDefaultInfo(List.of(user), guard));
        when(mapper.toEntity(request, guard)).thenReturn(warning);
        when(repository.saveAndFlush(warning)).thenReturn(warning);
        when(occurrenceService.getOccurrenceResponse(warning, vehicle)).thenReturn(mock(DefaultOccurrenceResponseDTO.class));
        when(mapper.toCreateResponse(eq(warning), any())).thenReturn(mock(CreateWarningResponseDTO.class));
        when(notificationMapper.toNotification(anyList(), eq(warning), anyString()))
                .thenReturn(new SendOccurrenceNotificationEvent(List.of(1L), "message", UUID.randomUUID(), List.of("User"), List.of("u@weg.net")));

        assertNotNull(service.registerWarningOccurrence(request, new JWTUserData(guard.getUuid(), "g@weg.net", List.of("ROLE_GUARD"), "Guard")));
        assertNotNull(warning.getDateHour());
        assertEquals(List.of(user), warning.getVehicleUsers());
        verify(occurrenceService).fiveOccurrenceWarn(List.of(user));
        verify(publisher).publishEvent(any(Object.class));
    }

    @Test
    void updatesWarningOrRejectsUnknownOccurrence() {
        UUID uuid = UUID.randomUUID();
        UpdateWarningRequestDTO request = mock(UpdateWarningRequestDTO.class);
        GetWarningResponseDTO response = mock(GetWarningResponseDTO.class);
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(warning));
        when(mapper.toGetResponse(warning)).thenReturn(response);

        assertSame(response, service.updateWarning(uuid, request));
        verify(mapper).updateFromDto(request, warning);
        verify(repository).save(warning);

        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateWarning(uuid, request));
    }
}

package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.TrafficAccidentMapper;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.TrafficAccident;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.trafficaccident.*;
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

class TrafficAccidentServiceTest {
    private OccurrenceRepository repository;
    private OccurrenceService occurrenceService;
    private TrafficAccidentMapper mapper;
    private TrafficAccidentService service;
    private TrafficAccident occurrence;

    @BeforeEach
    void setUp() {
        repository = mock(OccurrenceRepository.class);
        occurrenceService = mock(OccurrenceService.class);
        mapper = mock(TrafficAccidentMapper.class);
        service = new TrafficAccidentService(repository, occurrenceService, mock(ApplicationEventPublisher.class), mapper, mock(OccurrenceNotificationMapper.class));
        occurrence = mock(TrafficAccident.class);
        when(occurrence.getVehicleUsers()).thenReturn(new java.util.ArrayList<>());
    }

    @Test
    void registersTrafficAccidentOccurrence() {
        CreateTrafficAccidentRequestDTO request = mock(CreateTrafficAccidentRequestDTO.class);
        var defaults = mock(com.weg.WEGpark.park.internal.dto.occurrence.defaults.CreateOccurrenceRequestDTO.class);
        when(request.defaults()).thenReturn(defaults);
        when(defaults.plate()).thenReturn("ABC1234");
        Vehicle vehicle = new Vehicle("ABC1234", "M", "B", "C");
        VehicleUser association = new VehicleUser(new ParkUser(1L, UUID.randomUUID(), "u@weg.net", "1", "User"), vehicle);
        Guard guard = new Guard(2L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "2", "A", "Boss");
        when(occurrenceService.findRegisterBasics(eq("ABC1234"), any())).thenReturn(new RegisterDefaultInfo(List.of(association), guard));
        when(mapper.toEntity(request, guard)).thenReturn(occurrence);
        when(repository.saveAndFlush(occurrence)).thenReturn(occurrence);
        when(occurrenceService.getOccurrenceResponse(occurrence, vehicle)).thenReturn(mock(DefaultOccurrenceResponseDTO.class));
        when(mapper.toCreateResponse(eq(occurrence), any())).thenReturn(mock(CreateTrafficAccidentResponseDTO.class));

        assertNotNull(service.registerTrafficAccidentOccurrence(request, new JWTUserData(guard.getUuid(), "g@weg.net", List.of("ROLE_GUARD"), "Guard")));
        verify(occurrenceService).fiveOccurrenceWarn(List.of(association));
    }

    @Test
    void updatesTrafficAccidentOrRejectsUnknownOccurrence() {
        UUID uuid = UUID.randomUUID();
        UpdateTrafficAccidentRequestDTO request = mock(UpdateTrafficAccidentRequestDTO.class);
        GetTrafficAccidentResponseDTO response = mock(GetTrafficAccidentResponseDTO.class);
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(occurrence));
        when(mapper.toGetResponse(occurrence)).thenReturn(response);
        assertSame(response, service.updateTrafficAccident(uuid, request));
        verify(repository).save(occurrence);
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateTrafficAccident(uuid, request));
    }
}

package com.weg.WEGpark.park.internal.app.occurrence.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.park.internal.app.occurrence.dto.RegisterDefaultInfo;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.IllegalParkingMapper;
import com.weg.WEGpark.park.internal.app.occurrence.mapper.OccurrenceNotificationMapper;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.OccurrenceType;
import com.weg.WEGpark.park.internal.domain.enums.occurrence.ParkingSpaceType;
import com.weg.WEGpark.park.internal.domain.model.occurrence.IllegalParking;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.occurrence.defaults.DefaultOccurrenceResponseDTO;
import com.weg.WEGpark.park.internal.dto.occurrence.illegalparking.*;
import com.weg.WEGpark.park.internal.infra.repository.IllegalParkingRepository;
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

class IllegalParkingServiceTest {
    private IllegalParkingRepository repository;
    private OccurrenceService occurrenceService;
    private IllegalParkingMapper mapper;
    private IllegalParkingService service;
    private IllegalParking occurrence;

    @BeforeEach
    void setUp() {
        repository = mock(IllegalParkingRepository.class);
        occurrenceService = mock(OccurrenceService.class);
        mapper = mock(IllegalParkingMapper.class);
        service = new IllegalParkingService(mapper, mock(OccurrenceNotificationMapper.class), occurrenceService, mock(ApplicationEventPublisher.class), repository);
        occurrence = new IllegalParking("gate", "local", OccurrenceType.ILLEGAL_PARKING, ParkingSpaceType.COMMON, "description");
    }

    @Test
    void registersIllegalParkingOccurrence() {
        CreateIllegalParkingRequestDTO request = mock(CreateIllegalParkingRequestDTO.class);
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
        when(mapper.toCreateResponse(eq(occurrence), any())).thenReturn(mock(CreateIllegalParkingResponseDTO.class));

        assertNotNull(service.registerIllegalParkingOccurrence(request, new JWTUserData(guard.getUuid(), "g@weg.net", List.of("ROLE_GUARD"), "Guard")));
        assertNotNull(occurrence.getDateHour());
        verify(occurrenceService).fiveOccurrenceWarn(List.of(association));
    }

    @Test
    void updatesIllegalParkingOrRejectsUnknownOccurrence() {
        UUID uuid = UUID.randomUUID();
        UpdateIllegalParkingRequestDTO request = mock(UpdateIllegalParkingRequestDTO.class);
        GetIllegalParkingResponseDTO response = mock(GetIllegalParkingResponseDTO.class);
        when(repository.findByUuid(uuid)).thenReturn(Optional.of(occurrence));
        when(mapper.toGetResponse(occurrence)).thenReturn(response);
        assertSame(response, service.updateIllegalParking(uuid, request));
        verify(repository).save(occurrence);
        when(repository.findByUuid(uuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateIllegalParking(uuid, request));
    }
}

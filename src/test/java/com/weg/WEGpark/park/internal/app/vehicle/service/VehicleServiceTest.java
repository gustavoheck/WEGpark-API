package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.ParkUserMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleEventMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociateWithVehicleResponseDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.association.AssociationNotificationRequestDTO;
import com.weg.WEGpark.park.internal.dto.vehicle.defaults.*;
import com.weg.WEGpark.park.internal.dto.vehicle.filter.FilterVehicleRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {
    private VehicleMapper vehicleMapper;
    private ParkUserMapper parkUserMapper;
    private VehicleRepository vehicleRepository;
    private VehicleUserRepository vehicleUserRepository;
    private ParkUserRepository parkUserRepository;
    private ApplicationEventPublisher publisher;
    private VehicleEventMapper eventMapper;
    private VehicleUserMapper vehicleUserMapper;
    private VehicleService service;
    private ParkUser user;
    private JWTUserData token;

    @BeforeEach
    void setUp() {
        vehicleMapper = mock(VehicleMapper.class);
        parkUserMapper = mock(ParkUserMapper.class);
        vehicleRepository = mock(VehicleRepository.class);
        vehicleUserRepository = mock(VehicleUserRepository.class);
        parkUserRepository = mock(ParkUserRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        eventMapper = mock(VehicleEventMapper.class);
        vehicleUserMapper = mock(VehicleUserMapper.class);
        service = new VehicleService(vehicleMapper, parkUserMapper, vehicleRepository, vehicleUserRepository, parkUserRepository, publisher, eventMapper, vehicleUserMapper);
        user = new ParkUser(1L, UUID.randomUUID(), "user@weg.net", "1", "User");
        token = new JWTUserData(user.getUuid(), user.getEmail(), List.of("ROLE_PARK"), user.getName());
    }

    @Test
    void registersNewVehicleAndNormalizesPlate() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("abc-1234", "Model", "Brand", "Blue");
        Vehicle vehicle = new Vehicle(request.plate(), request.model(), request.brand(), request.color());
        GetVehicleResponseDTO response = new GetVehicleResponseDTO(UUID.randomUUID(), "ABC1234", "Model", "Brand", "Blue", List.of());
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid(request.plate(), user.getUuid())).thenReturn(Optional.empty());
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive(request.plate(), true, true)).thenReturn(false);
        when(vehicleRepository.existsByPlate(request.plate())).thenReturn(false);
        when(parkUserRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(vehicleMapper.toEntity(request)).thenReturn(vehicle);
        when(vehicleMapper.toGetResponse(eq(vehicle), anyList())).thenReturn(response);

        assertSame(response, service.registerVehicle(request, token));
        assertEquals("ABC1234", vehicle.getPlate());
        verify(vehicleRepository).saveAndFlush(vehicle);
        verify(vehicleUserRepository).saveAndFlush(any(VehicleUser.class));
    }

    @Test
    void reactivatesExistingInactiveAssociationBeforeCreatingVehicle() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("ABC1234", "Model", "Brand", "Blue");
        VehicleUser association = new VehicleUser(user, new Vehicle("ABC1234", "M", "B", "C"));
        association.setActive(false);
        association.setVehicleOwner(false);
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid(request.plate(), user.getUuid())).thenReturn(Optional.of(association));
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive(request.plate(), true, true)).thenReturn(false);

        assertDoesNotThrow(() -> service.registerVehicle(request, token));
        assertAll(() -> assertTrue(association.getActive()), () -> assertTrue(association.getVehicleOwner()));
        verify(vehicleUserRepository).save(association);
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void rejectsAlreadyOwnedVehicle() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("ABC1234", "Model", "Brand", "Blue");
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid(anyString(), any())).thenReturn(Optional.empty());
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive(request.plate(), true, true)).thenReturn(true);
        when(vehicleRepository.existsByPlate(request.plate())).thenReturn(true);

        assertThrows(VehicleAlreadyRegisteredException.class, () -> service.registerVehicle(request, token));
    }

    @Test
    void associatesUserFromNotificationEvent() {
        UUID notification = UUID.randomUUID();
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setId(4L);
        doAnswer(invocation -> {
            FindAssociationNotificationEvent event = invocation.getArgument(0);
            event.eventResponse().complete(new FindAssociationNotificationResponse(1L, 4L));
            return null;
        }).when(publisher).publishEvent(any(FindAssociationNotificationEvent.class));
        when(parkUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(4L)).thenReturn(Optional.of(vehicle));
        AssociateWithVehicleResponseDTO response = new AssociateWithVehicleResponseDTO(user.getUuid(), user.getEmail(), user.getName());
        when(parkUserMapper.toAssociationResponse(user)).thenReturn(response);

        assertSame(response, service.associateToRegisteredVehicle(notification));
        verify(vehicleUserRepository).save(argThat(association -> !association.getVehicleOwner()));
    }

    @Test
    void sendsAssociationNotificationToVehicleOwner() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        ParkUser owner = new ParkUser(2L, UUID.randomUUID(), "owner@weg.net", "1", "Owner");
        VehicleUser ownerAssociation = new VehicleUser(owner, vehicle);
        ownerAssociation.setVehicleOwner(true);
        vehicle.setParkUsers(List.of(ownerAssociation));
        AssociateToVehicleNotificationEvent event = new AssociateToVehicleNotificationEvent(owner.getId(), user.getId(), user.getName(), vehicle.getId(), vehicle.getBrand(), vehicle.getModel());
        when(parkUserRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(vehicleRepository.findByPlate("ABC1234")).thenReturn(Optional.of(vehicle));
        when(eventMapper.toEvent(user, vehicle, owner)).thenReturn(event);

        service.SendNotificationForAssociate(new AssociationNotificationRequestDTO("ABC1234"), token);

        verify(eventMapper).toEvent(user, vehicle, owner);
        verify(publisher).publishEvent(event);
    }

    @Test
    void findsVehiclesUpdatesVehicleAndListsCurrentUsersVehicles() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setUuid(UUID.randomUUID());
        VehicleUser association = new VehicleUser(user, vehicle);
        association.setVehicleOwner(true);
        vehicle.setParkUsers(List.of(association));
        GetVehicleResponseDTO response = new GetVehicleResponseDTO(vehicle.getUuid(), vehicle.getPlate(), vehicle.getModel(), vehicle.getBrand(), vehicle.getColor(), List.of());
        var pageable = PageRequest.of(0, 10);
        when(vehicleRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable))).thenReturn(new PageImpl<>(List.of(vehicle), pageable, 1));
        when(vehicleMapper.toGetResponse(eq(vehicle), anyList())).thenReturn(response);
        when(vehicleUserRepository.findByUuidParkUser(user.getUuid())).thenReturn(List.of(association));
        when(vehicleRepository.findByUuid(vehicle.getUuid())).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toUpdateResponse(vehicle)).thenReturn(new UpdateVehicleResponseDTO(vehicle.getUuid(), "ABC1234", "Model", "Brand", "Blue"));

        assertEquals(1, service.findVehicle(new FilterVehicleRequestDTO("ABC1234", null, null, null, null), pageable).getTotalElements());
        assertEquals(List.of(response), service.findMyVehicles(token));
        assertEquals("ABC1234", service.updateVehicle(vehicle.getUuid(), new UpdateVehicleRequestDTO(null, null, null, null)).plate());
        verify(vehicleMapper).updateFromDto(any(UpdateVehicleRequestDTO.class), same(vehicle));
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void rejectsUnknownVehicleForUpdate() {
        UUID vehicleUuid = UUID.randomUUID();
        when(vehicleRepository.findByUuid(vehicleUuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateVehicle(vehicleUuid, new UpdateVehicleRequestDTO(null, null, null, null)));
    }
}

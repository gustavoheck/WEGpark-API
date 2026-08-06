package com.weg.WEGpark.park.internal.app.vehicle.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.notification.FindAssociationNotificationResponse;
import com.weg.WEGpark.park.AssociateToVehicleNotificationEvent;
import com.weg.WEGpark.park.FindAssociationNotificationEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.ParkUserMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VehicleUserMapper;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyAssociatedWithUserException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyOwnedByUserException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleEventMapper;
import com.weg.WEGpark.park.internal.app.vehicle.mapper.VehicleMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
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
import org.springframework.security.access.AccessDeniedException;
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
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid("ABC1234", user.getUuid())).thenReturn(Optional.empty());
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive("ABC1234", true, true)).thenReturn(false);
        when(vehicleRepository.existsByPlate("ABC1234")).thenReturn(false);
        when(parkUserRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(vehicleMapper.toEntity(request)).thenReturn(vehicle);
        when(vehicleMapper.toGetResponse(eq(vehicle), anyList())).thenReturn(response);

        assertSame(response, service.registerVehicle(request, token));
        assertEquals("ABC1234", vehicle.getPlate());
        verify(vehicleRepository).saveAndFlush(vehicle);
        verify(vehicleUserRepository).saveAndFlush(any(VehicleUser.class));
    }

    @Test
    void mapsVehicleUsersWithSubtypeDataAndAssociationStatus() {
        VehicleUserMapper mapper = new VehicleUserMapper() { };
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");

        Collaborator collaborator = new Collaborator(
                1L, UUID.randomUUID(), "collaborator@weg.net", "1111", "Collaborator", "B1", "Factory");
        collaborator.setUserType(ParkUserType.COLLABORATOR);
        VehicleUser collaboratorAssociation = new VehicleUser(collaborator, vehicle);
        collaboratorAssociation.setVehicleOwner(true);

        Visitor visitor = new Visitor(
                2L, UUID.randomUUID(), "visitor@weg.net", "2222", "Visitor", "Company", "12345678900");
        visitor.setUserType(ParkUserType.VISITOR);
        VehicleUser visitorAssociation = new VehicleUser(visitor, vehicle);
        visitorAssociation.setVehicleOwner(false);
        visitorAssociation.setActive(false);

        Guard guard = new Guard(
                3L, UUID.randomUUID(), "guard@weg.net", "3333", "Guard", "B2", "Gate", "Boss");
        guard.setUserType(ParkUserType.GUARD);
        VehicleUser guardAssociation = new VehicleUser(guard, vehicle);
        guardAssociation.setVehicleOwner(false);

        GetVehicleUserResponseDTO collaboratorResponse = mapper.toResponse(collaboratorAssociation);
        GetVehicleUserResponseDTO visitorResponse = mapper.toResponse(visitorAssociation);
        GetVehicleUserResponseDTO guardResponse = mapper.toResponse(guardAssociation);

        assertAll(
                () -> assertEquals(collaborator.getUuid(), collaboratorResponse.userUuid()),
                () -> assertTrue(collaboratorResponse.isOwner()),
                () -> assertTrue(collaboratorResponse.associationActive()),
                () -> assertEquals("1111", collaboratorResponse.telephone()),
                () -> assertEquals("Collaborator", collaboratorResponse.name()),
                () -> assertEquals(ParkUserType.COLLABORATOR, collaboratorResponse.userType()),
                () -> assertEquals("B1", collaboratorResponse.badgeNumber()),
                () -> assertEquals("Factory", collaboratorResponse.location()),
                () -> assertNull(collaboratorResponse.boss()),
                () -> assertNull(collaboratorResponse.company()),
                () -> assertFalse(visitorResponse.associationActive()),
                () -> assertEquals("Company", visitorResponse.company()),
                () -> assertNull(visitorResponse.badgeNumber()),
                () -> assertEquals("Boss", guardResponse.boss()),
                () -> assertEquals("B2", guardResponse.badgeNumber()),
                () -> assertEquals("Gate", guardResponse.location())
        );
    }

    @Test
    void reactivatesExistingInactiveAssociationBeforeCreatingVehicle() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO(" abc-1234 ", "Model", "Brand", "Blue");
        VehicleUser association = new VehicleUser(user, new Vehicle("ABC1234", "M", "B", "C"));
        association.setActive(false);
        association.setVehicleOwner(true);
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid("ABC1234", user.getUuid())).thenReturn(Optional.of(association));
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive("ABC1234", true, true)).thenReturn(false);

        assertDoesNotThrow(() -> service.registerVehicle(request, token));
        assertAll(() -> assertTrue(association.getActive()), () -> assertTrue(association.getVehicleOwner()));
        verify(vehicleUserRepository).save(association);
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void rejectsAlreadyOwnedVehicle() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("abc1234", "Model", "Brand", "Blue");
        VehicleUser association = new VehicleUser(user, new Vehicle("ABC1234", "Model", "Brand", "Blue"));
        association.setVehicleOwner(true);
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid("ABC1234", user.getUuid()))
                .thenReturn(Optional.of(association));
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive("ABC1234", true, true)).thenReturn(true);

        assertThrows(VehicleAlreadyOwnedByUserException.class, () -> service.registerVehicle(request, token));
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void rejectsVehicleAlreadyAssociatedWithLoggedUser() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("abc1234", "Model", "Brand", "Blue");
        VehicleUser association = new VehicleUser(user, new Vehicle("ABC1234", "Model", "Brand", "Blue"));
        association.setVehicleOwner(false);
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid("ABC1234", user.getUuid()))
                .thenReturn(Optional.of(association));
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive("ABC1234", true, true)).thenReturn(true);

        assertThrows(VehicleAlreadyAssociatedWithUserException.class, () -> service.registerVehicle(request, token));
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    void rejectsRegisteredVehicleForUserWithoutAssociation() {
        CreateVehicleRequestDTO request = new CreateVehicleRequestDTO("abc1234", "Model", "Brand", "Blue");
        when(vehicleUserRepository.findByVehiclePlateAndParkUserUuid(anyString(), any())).thenReturn(Optional.empty());
        when(vehicleUserRepository.existsByVehiclePlateAndVehicleOwnerAndActive("ABC1234", true, true)).thenReturn(true);
        when(vehicleRepository.existsByPlate("ABC1234")).thenReturn(true);

        assertThrows(VehicleAlreadyRegisteredException.class, () -> service.registerVehicle(request, token));
    }

    @Test
    void associatesUserFromNotificationEvent() {
        UUID notification = UUID.randomUUID();
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setId(4L);
        doAnswer(invocation -> {
            FindAssociationNotificationEvent event = invocation.getArgument(0);
            assertEquals(token.uuid(), event.notificatedUserUuid());
            event.eventResponse().complete(new FindAssociationNotificationResponse(1L, 4L));
            return null;
        }).when(publisher).publishEvent(any(FindAssociationNotificationEvent.class));
        when(parkUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(4L)).thenReturn(Optional.of(vehicle));
        AssociateWithVehicleResponseDTO response = new AssociateWithVehicleResponseDTO(user.getUuid(), user.getEmail(), user.getName());
        when(parkUserMapper.toAssociationResponse(user)).thenReturn(response);

        assertSame(response, service.associateToRegisteredVehicle(notification, token));
        verify(vehicleUserRepository).save(argThat(association -> !association.getVehicleOwner()));
    }

    @Test
    void reactivatesInactiveAssociationFromNotificationEvent() {
        UUID notification = UUID.randomUUID();
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setId(4L);
        vehicle.setUuid(UUID.randomUUID());
        VehicleUser inactiveAssociation = new VehicleUser(user, vehicle);
        inactiveAssociation.setVehicleOwner(false);
        inactiveAssociation.setActive(false);
        doAnswer(invocation -> {
            FindAssociationNotificationEvent event = invocation.getArgument(0);
            event.eventResponse().complete(new FindAssociationNotificationResponse(1L, 4L));
            return null;
        }).when(publisher).publishEvent(any(FindAssociationNotificationEvent.class));
        when(parkUserRepository.findById(1L)).thenReturn(Optional.of(user));
        when(vehicleRepository.findById(4L)).thenReturn(Optional.of(vehicle));
        when(vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), user.getUuid()))
                .thenReturn(Optional.of(inactiveAssociation));
        AssociateWithVehicleResponseDTO response = new AssociateWithVehicleResponseDTO(
                user.getUuid(), user.getEmail(), user.getName()
        );
        when(parkUserMapper.toAssociationResponse(user)).thenReturn(response);

        assertSame(response, service.associateToRegisteredVehicle(notification, token));
        assertTrue(inactiveAssociation.getActive());
        verify(vehicleUserRepository).save(inactiveAssociation);
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

        service.SendNotificationForAssociate(new AssociationNotificationRequestDTO(" abc-1234 "), token);

        verify(eventMapper).toEvent(user, vehicle, owner);
        verify(publisher).publishEvent(event);
    }

    @Test
    void rejectsAssociationNotificationRequestedByVehicleOwner() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setUuid(UUID.randomUUID());
        VehicleUser ownerAssociation = new VehicleUser(user, vehicle);
        ownerAssociation.setVehicleOwner(true);
        vehicle.setParkUsers(List.of(ownerAssociation));
        when(parkUserRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(vehicleRepository.findByPlate("ABC1234")).thenReturn(Optional.of(vehicle));
        when(vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), user.getUuid()))
                .thenReturn(Optional.of(ownerAssociation));

        assertThrows(VehicleAlreadyOwnedByUserException.class, () ->
                service.SendNotificationForAssociate(
                        new AssociationNotificationRequestDTO("ABC1234"), token
                )
        );
        verify(publisher, never()).publishEvent(any(AssociateToVehicleNotificationEvent.class));
    }

    @Test
    void rejectsAssociationNotificationRequestedByAssociatedUser() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setUuid(UUID.randomUUID());
        ParkUser owner = new ParkUser(2L, UUID.randomUUID(), "owner@weg.net", "1", "Owner");
        VehicleUser ownerAssociation = new VehicleUser(owner, vehicle);
        ownerAssociation.setVehicleOwner(true);
        VehicleUser userAssociation = new VehicleUser(user, vehicle);
        userAssociation.setVehicleOwner(false);
        vehicle.setParkUsers(List.of(ownerAssociation, userAssociation));
        when(parkUserRepository.findByUuid(user.getUuid())).thenReturn(Optional.of(user));
        when(vehicleRepository.findByPlate("ABC1234")).thenReturn(Optional.of(vehicle));
        when(vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), user.getUuid()))
                .thenReturn(Optional.of(userAssociation));

        assertThrows(VehicleAlreadyAssociatedWithUserException.class, () ->
                service.SendNotificationForAssociate(
                        new AssociationNotificationRequestDTO("ABC1234"), token
                )
        );
        verify(publisher, never()).publishEvent(any(AssociateToVehicleNotificationEvent.class));
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
        when(vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), user.getUuid())).thenReturn(Optional.of(association));

        assertEquals(1, service.findVehicle(new FilterVehicleRequestDTO("ABC1234", null, null, null, null), pageable).getTotalElements());
        assertEquals(List.of(response), service.findMyVehicles(token));
        assertEquals("ABC1234", service.updateVehicle(vehicle.getUuid(), new UpdateVehicleRequestDTO(null, null, null, null), token).plate());
        verify(vehicleMapper).updateFromDto(any(UpdateVehicleRequestDTO.class), same(vehicle));
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void rejectsParkUserNotAssociatedWithVehicle() {
        UUID vehicleUuid = UUID.randomUUID();
        when(vehicleUserRepository.findByVehicleUuidAndParkUserUuid(vehicleUuid, user.getUuid()))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                service.updateVehicle(
                        vehicleUuid,
                        new UpdateVehicleRequestDTO(null, null, null, null),
                        token
                )
        );
        verify(vehicleRepository, never()).findByUuid(vehicleUuid);
    }

    @Test
    void allowsGuardToUpdateVehicleWithoutAssociation() {
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        vehicle.setUuid(UUID.randomUUID());
        JWTUserData guardToken = new JWTUserData(
                UUID.randomUUID(),
                "guard@weg.net",
                List.of("ROLE_GUARD"),
                "Guard"
        );
        UpdateVehicleResponseDTO response = new UpdateVehicleResponseDTO(
                vehicle.getUuid(), "ABC1234", "Updated", "Brand", "Blue"
        );
        when(vehicleRepository.findByUuid(vehicle.getUuid())).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toUpdateResponse(vehicle)).thenReturn(response);

        assertSame(response, service.updateVehicle(
                vehicle.getUuid(),
                new UpdateVehicleRequestDTO(" abc-1234 ", "Updated", null, null),
                guardToken
        ));
        verify(vehicleUserRepository, never()).findByVehicleUuidAndParkUserUuid(any(), any());
        assertEquals("ABC1234", vehicle.getPlate());
    }

    @Test
    void rejectsUnknownVehicleForUpdate() {
        UUID vehicleUuid = UUID.randomUUID();
        JWTUserData guardToken = new JWTUserData(
                UUID.randomUUID(),
                "guard@weg.net",
                List.of("ROLE_GUARD"),
                "Guard"
        );
        when(vehicleRepository.findByUuid(vehicleUuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateVehicle(
                vehicleUuid, new UpdateVehicleRequestDTO(null, null, null, null), guardToken
        ));
    }
}

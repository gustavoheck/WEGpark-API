package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleUserServiceTest {
    private VehicleUserRepository repository;
    private VehicleUserService service;
    private UUID userUuid;
    private UUID vehicleUuid;

    @BeforeEach
    void setUp() {
        repository = mock(VehicleUserRepository.class);
        service = new VehicleUserService(repository);
        userUuid = UUID.randomUUID();
        vehicleUuid = UUID.randomUUID();
    }

    @Test
    void deactivatesOnlyTheLoggedNonOwnerAssociation() {
        VehicleUser association = association(false);
        when(repository.findByVehicleUuidAndParkUserUuid(vehicleUuid, userUuid)).thenReturn(Optional.of(association));

        service.disassociateVehicle(vehicleUuid, token());

        assertFalse(association.getActive());
    }

    @Test
    void deactivatesAllAssociationsWhenOwnerDisassociates() {
        VehicleUser owner = association(true);
        VehicleUser shared = association(false);
        owner.getVehicle().setParkUsers(List.of(owner, shared));
        when(repository.findByVehicleUuidAndParkUserUuid(vehicleUuid, userUuid)).thenReturn(Optional.of(owner));

        service.disassociateVehicle(vehicleUuid, token());

        assertAll(() -> assertFalse(owner.getActive()), () -> assertFalse(shared.getActive()));
    }

    @Test
    void rejectsDisassociationForUnknownAssociation() {
        when(repository.findByVehicleUuidAndParkUserUuid(vehicleUuid, userUuid)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.disassociateVehicle(vehicleUuid, token()));
    }

    private VehicleUser association(boolean owner) {
        ParkUser user = new ParkUser(1L, userUuid, "user@weg.net", "1", "User");
        Vehicle vehicle = new Vehicle("ABC1234", "Model", "Brand", "Blue");
        VehicleUser association = new VehicleUser(user, vehicle);
        association.setVehicleOwner(owner);
        return association;
    }

    private JWTUserData token() {
        return new JWTUserData(userUuid, "user@weg.net", List.of("ROLE_PARK"), "User");
    }
}

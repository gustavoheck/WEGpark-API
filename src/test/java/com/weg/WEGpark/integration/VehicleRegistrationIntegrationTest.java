package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleRegistrationIntegrationTest extends AbstractPostgresIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private TokenConfig tokenConfig;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ParkUserRepository parkUserRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleUserRepository vehicleUserRepository;

    @Test
    void createsVehicleAndOwnerAssociationThroughAuthenticatedHttpFlow() throws Exception {
        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();
        User user = new User("vehicle-owner@weg.net", "encoded");
        user.setRole(parkRole);
        user.setActive(true);
        user.setEmailValidated(true);
        user = userRepository.saveAndFlush(user);
        Collaborator collaborator = new Collaborator(user.getId(), user.getUuid(), user.getEmail(), "11999999999", "Vehicle Owner", "123456", "A");
        parkUserRepository.saveAndFlush(collaborator);
        String jwt = tokenConfig.generateToken(user, collaborator.getName());

        mockMvc.perform(post("/vehicle")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"plate\":\"abc1234\",\"model\":\"Civic\",\"brand\":\"Honda\",\"color\":\"Blue\"}"))
                .andExpect(status().isCreated());

        assertTrue(vehicleRepository.findByPlate("ABC1234").isPresent());
        assertTrue(vehicleUserRepository.findByParkUserId(collaborator.getId()).stream().anyMatch(vehicleUser -> vehicleUser.getVehicleOwner()));
    }

    @Test
    void differentiatesVehicleOwnerFromAssociatedUserDuringRegistration() throws Exception {
        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();

        User ownerAuth = new User("registration-owner@weg.net", "encoded");
        ownerAuth.setRole(parkRole);
        ownerAuth.setActive(true);
        ownerAuth.setEmailValidated(true);
        ownerAuth = userRepository.saveAndFlush(ownerAuth);
        Collaborator owner = new Collaborator(
                ownerAuth.getId(), ownerAuth.getUuid(), ownerAuth.getEmail(),
                "11999999991", "Registration Owner", "REG-OWNER", "A"
        );
        parkUserRepository.saveAndFlush(owner);

        User associatedAuth = new User("registration-associated@weg.net", "encoded");
        associatedAuth.setRole(parkRole);
        associatedAuth.setActive(true);
        associatedAuth.setEmailValidated(true);
        associatedAuth = userRepository.saveAndFlush(associatedAuth);
        Collaborator associatedUser = new Collaborator(
                associatedAuth.getId(), associatedAuth.getUuid(), associatedAuth.getEmail(),
                "11999999992", "Registration Associated", "REG-ASSOCIATED", "A"
        );
        parkUserRepository.saveAndFlush(associatedUser);

        Vehicle vehicle = vehicleRepository.saveAndFlush(
                new Vehicle("DIF1234", "Civic", "Honda", "Blue")
        );
        VehicleUser ownerAssociation = new VehicleUser(owner, vehicle);
        ownerAssociation.setVehicleOwner(true);
        vehicleUserRepository.saveAndFlush(ownerAssociation);
        VehicleUser userAssociation = new VehicleUser(associatedUser, vehicle);
        userAssociation.setVehicleOwner(false);
        vehicleUserRepository.saveAndFlush(userAssociation);

        String requestBody = "{\"plate\":\"DIF1234\",\"model\":\"Civic\","
                + "\"brand\":\"Honda\",\"color\":\"Blue\"}";

        String ownerJwt = tokenConfig.generateToken(ownerAuth, owner.getName());
        mockMvc.perform(post("/vehicle")
                        .header("Authorization", "Bearer " + ownerJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("This vehicle is already registered by the logged user"));

        String associatedJwt = tokenConfig.generateToken(associatedAuth, associatedUser.getName());
        mockMvc.perform(post("/vehicle")
                        .header("Authorization", "Bearer " + associatedJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("The logged user is already associated with this vehicle"));
    }

    @Test
    void restrictsParkUpdateToAssociatedVehiclesAndAllowsGuardUpdate() throws Exception {
        Vehicle vehicle = new Vehicle("SEC1234", "Original", "Honda", "Blue");
        vehicleRepository.saveAndFlush(vehicle);

        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();
        User parkUser = new User("vehicle-outsider@weg.net", "encoded");
        parkUser.setRole(parkRole);
        parkUser.setActive(true);
        parkUser.setEmailValidated(true);
        parkUser = userRepository.saveAndFlush(parkUser);
        Collaborator collaborator = new Collaborator(parkUser.getId(), parkUser.getUuid(), parkUser.getEmail(),
                "11999999997", "Vehicle Outsider", "654321", "B");
        parkUserRepository.saveAndFlush(collaborator);
        String parkJwt = tokenConfig.generateToken(parkUser, collaborator.getName());

        mockMvc.perform(put("/vehicle/{uuid}", vehicle.getUuid())
                        .header("Authorization", "Bearer " + parkJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"model":"Park update"}
                                """))
                .andExpect(status().isForbidden());
        assertEquals("Original", vehicleRepository.findByUuid(vehicle.getUuid()).orElseThrow().getModel());

        Role guardRole = roleRepository.findByRole(RolesType.ROLE_GUARD).orElseThrow();
        User guardUser = new User("vehicle-guard@weg.net", "encoded");
        guardUser.setRole(guardRole);
        guardUser.setActive(true);
        guardUser.setEmailValidated(true);
        guardUser = userRepository.saveAndFlush(guardUser);
        Guard guard = new Guard(guardUser.getId(), guardUser.getUuid(), guardUser.getEmail(),
                "11999999996", "Vehicle Guard", "G-02", "Gate B", "Security");
        parkUserRepository.saveAndFlush(guard);
        String guardJwt = tokenConfig.generateToken(guardUser, guard.getName());

        mockMvc.perform(put("/vehicle/{uuid}", vehicle.getUuid())
                        .header("Authorization", "Bearer " + guardJwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"model":"Guard update"}
                                """))
                .andExpect(status().isOk());
        assertEquals("Guard update", vehicleRepository.findByUuid(vehicle.getUuid()).orElseThrow().getModel());
    }
}

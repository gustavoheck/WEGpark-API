package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.notification.internal.app.notification.service.EmailService;
import com.weg.WEGpark.park.internal.domain.model.occurrence.Occurrence;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.infra.repository.OccurrenceRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class WarningRegistrationIntegrationTest extends AbstractPostgresIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private TokenConfig tokenConfig;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ParkUserRepository parkUserRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private VehicleUserRepository vehicleUserRepository;
    @Autowired private OccurrenceRepository occurrenceRepository;
    @MockitoBean private EmailService emailService;

    @Test
    void registersWarningForVehicleThroughAuthenticatedHttpFlow() throws Exception {
        Guard guard = saveGuard();
        Collaborator owner = saveVehicleAndOwner();
        User guardUser = userRepository.findById(guard.getId()).orElseThrow();
        String jwt = tokenConfig.generateToken(guardUser, guard.getName());

        mockMvc.perform(post("/occurrence/warning")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "defaults":{"location":"Gate A","gate":"A","plate":"WAR1234"},
                                  "warningType":"OTHER",
                                  "description":"Vehicle parked with a safety issue"
                                }
                                """))
                .andExpect(status().isCreated());

        assertEquals(1, occurrenceRepository.count());
        Occurrence occurrence = occurrenceRepository.findAll().getFirst();
        assertEquals(guard.getId(), occurrence.getGuard().getId());
        assertEquals("Gate A", occurrence.getLocation());

        User ownerUser = userRepository.findById(owner.getId()).orElseThrow();
        String ownerJwt = tokenConfig.generateToken(ownerUser, owner.getName());
        mockMvc.perform(get("/occurrence/me")
                        .header("Authorization", "Bearer " + ownerJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].uuid").value(occurrence.getUuid().toString()));

        Collaborator outsider = saveParkUser(
                "outsider-integration@weg.net", "11999999996", "Integration Outsider", "C-02"
        );
        User outsiderUser = userRepository.findById(outsider.getId()).orElseThrow();
        String outsiderJwt = tokenConfig.generateToken(outsiderUser, outsider.getName());
        mockMvc.perform(get("/occurrence/me")
                        .header("Authorization", "Bearer " + outsiderJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    private Guard saveGuard() {
        Role guardRole = roleRepository.findByRole(RolesType.ROLE_GUARD).orElseThrow();
        User user = new User("guard-integration@weg.net", "encoded");
        user.setRole(guardRole);
        user.setActive(true);
        user.setEmailValidated(true);
        user = userRepository.saveAndFlush(user);
        Guard guard = new Guard(user.getId(), user.getUuid(), user.getEmail(), "11999999998",
                "Integration Guard", "G-01", "Gate A", "Security");
        return parkUserRepository.saveAndFlush(guard);
    }

    private Collaborator saveVehicleAndOwner() {
        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();
        User ownerUser = new User("owner-integration@weg.net", "encoded");
        ownerUser.setRole(parkRole);
        ownerUser.setActive(true);
        ownerUser.setEmailValidated(true);
        ownerUser = userRepository.saveAndFlush(ownerUser);
        Collaborator owner = new Collaborator(ownerUser.getId(), ownerUser.getUuid(), ownerUser.getEmail(),
                "11999999997", "Integration Owner", "C-01", "A");
        owner = parkUserRepository.saveAndFlush(owner);
        Vehicle vehicle = vehicleRepository.saveAndFlush(new Vehicle("WAR1234", "Civic", "Honda", "Blue"));
        VehicleUser association = new VehicleUser(owner, vehicle);
        association.setVehicleOwner(true);
        vehicleUserRepository.saveAndFlush(association);
        return owner;
    }

    private Collaborator saveParkUser(String email, String phone, String name, String badgeNumber) {
        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();
        User user = new User(email, "encoded");
        user.setRole(parkRole);
        user.setActive(true);
        user.setEmailValidated(true);
        user = userRepository.saveAndFlush(user);
        Collaborator collaborator = new Collaborator(
                user.getId(), user.getUuid(), user.getEmail(), phone, name, badgeNumber, "A"
        );

        return parkUserRepository.saveAndFlush(collaborator);
    }
}

package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}

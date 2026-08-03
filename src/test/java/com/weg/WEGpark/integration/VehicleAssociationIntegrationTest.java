package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.notification.internal.domain.entities.VehicleAssociationNotification;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.VehicleUser;
import com.weg.WEGpark.park.internal.domain.model.vehicle.Vehicle;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleRepository;
import com.weg.WEGpark.park.internal.infra.repository.VehicleUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VehicleAssociationIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParkUserRepository parkUserRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleUserRepository vehicleUserRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void onlyOwnerCanConsumeAssociationNotificationOnce() throws Exception {
        Role parkRole = roleRepository.findByRole(RolesType.ROLE_PARK).orElseThrow();
        User ownerAuth = saveAuthUser("association-owner@weg.net", parkRole);
        Collaborator owner = saveParkUser(ownerAuth, "Association Owner", "ASSOC-01");
        User requesterAuth = saveAuthUser("association-requester@weg.net", parkRole);
        Collaborator requester = saveParkUser(requesterAuth, "Association Requester", "ASSOC-02");
        Vehicle vehicle = vehicleRepository.saveAndFlush(
                new Vehicle("ASC1234", "Civic", "Honda", "Blue")
        );
        VehicleUser ownerAssociation = new VehicleUser(owner, vehicle);
        ownerAssociation.setVehicleOwner(true);
        vehicleUserRepository.saveAndFlush(ownerAssociation);

        VehicleAssociationNotification notification = new VehicleAssociationNotification(
                ownerAuth.getId(), vehicle.getId(), requesterAuth.getId()
        );
        notification.setMessage("Association request");
        notification = notificationRepository.saveAndFlush(notification);

        String requesterJwt = tokenConfig.generateToken(requesterAuth, requester.getName());
        mockMvc.perform(post("/vehicle/associate/{uuid}", notification.getUuid())
                        .header("Authorization", "Bearer " + requesterJwt))
                .andExpect(status().isNotFound());
        assertFalse(findNotification(notification).getUsed());

        String ownerJwt = tokenConfig.generateToken(ownerAuth, owner.getName());
        mockMvc.perform(post("/vehicle/associate/{uuid}", notification.getUuid())
                        .header("Authorization", "Bearer " + ownerJwt))
                .andExpect(status().isCreated());

        assertTrue(findNotification(notification).getUsed());
        assertTrue(vehicleUserRepository
                .findByVehicleUuidAndParkUserUuid(vehicle.getUuid(), requester.getUuid())
                .isPresent());

        mockMvc.perform(post("/vehicle/associate/{uuid}", notification.getUuid())
                        .header("Authorization", "Bearer " + ownerJwt))
                .andExpect(status().isNotFound());
    }

    private User saveAuthUser(String email, Role role) {
        User user = new User(email, "encoded");
        user.setRole(role);
        user.setActive(true);
        user.setEmailValidated(true);
        return userRepository.saveAndFlush(user);
    }

    private Collaborator saveParkUser(User user, String name, String badgeNumber) {
        Collaborator collaborator = new Collaborator(
                user.getId(), user.getUuid(), user.getEmail(), "11999999994", name, badgeNumber, "A"
        );
        return parkUserRepository.saveAndFlush(collaborator);
    }

    private VehicleAssociationNotification findNotification(VehicleAssociationNotification notification) {
        return (VehicleAssociationNotification) notificationRepository
                .findByUuid(notification.getUuid())
                .orElseThrow();
    }
}

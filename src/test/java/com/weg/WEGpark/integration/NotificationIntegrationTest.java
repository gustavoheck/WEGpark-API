package com.weg.WEGpark.integration;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.notification.internal.app.notification.service.EmailService;
import com.weg.WEGpark.notification.internal.domain.entities.Notification;
import com.weg.WEGpark.notification.internal.domain.enums.NotificationType;
import com.weg.WEGpark.notification.internal.infra.repository.NotificationRepository;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TokenConfig tokenConfig;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RhRepository rhRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @MockitoBean
    private EmailService emailService;

    @Test
    void supportsRhNotificationThroughAuthUserForeignKey() throws Exception {
        Role rhRole = roleRepository.findByRole(RolesType.ROLE_RH).orElseThrow();
        User authUser = new User("rh-notification@weg.net", "encoded");
        authUser.setRole(rhRole);
        authUser.setActive(true);
        authUser.setEmailValidated(true);
        authUser = userRepository.saveAndFlush(authUser);
        Rh rh = new Rh(
                authUser.getId(),
                authUser.getUuid(),
                authUser.getEmail(),
                "11999999995",
                "Notification RH",
                "RH-NOTIFICATION"
        );
        rhRepository.saveAndFlush(rh);
        Notification notification = notificationRepository.saveAndFlush(
                new Notification(authUser.getId(), NotificationType.FIVE_OCCURRENCE, "RH notification")
        );
        String jwt = tokenConfig.generateToken(authUser, rh.getName());

        mockMvc.perform(get("/notification")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].uuid").value(notification.getUuid().toString()));

        mockMvc.perform(delete("/notification/{uuid}", notification.getUuid())
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertFalse(notificationRepository.existsById(notification.getId()));
    }
}

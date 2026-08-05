package com.weg.WEGpark.auth.internal.infra.security.config;

import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.SecurityEndpoints.class)
@Import({
        SecurityConfig.class,
        SecurityFilter.class,
        CorsConfig.class,
        SecurityConfigTest.SecurityTestConfig.class,
        SecurityConfigTest.SecurityEndpoints.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenConfig tokenConfig;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void permitsPublicAuthEndpointsWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/auth/login"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/auth/register/visitor"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/auth/reset-password/check-email"))
                .andExpect(status().isOk());
        mockMvc.perform(patch("/auth/reset-password"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/auth/validate-email/token"))
                .andExpect(status().isOk());
    }

    @Test
    void restrictsParkUserToParkResources() throws Exception {
        var parkUser = user("park").authorities(new SimpleGrantedAuthority(RolesType.ROLE_PARK.name()));

        mockMvc.perform(post("/vehicle").with(parkUser))
                .andExpect(status().isOk());
        mockMvc.perform(get("/vehicle/me").with(parkUser))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence/me").with(parkUser))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence").with(parkUser))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/occurrence/00000000-0000-0000-0000-000000000001").with(parkUser))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/notification").with(parkUser))
                .andExpect(status().isOk());
        mockMvc.perform(get("/vehicle").with(parkUser))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/occurrence/warning").with(parkUser))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/rh").with(parkUser))
                .andExpect(status().isForbidden());
    }

    @Test
    void restrictsGuardToOccurrenceAndGeneralVehicleResources() throws Exception {
        var guard = user("guard").authorities(new SimpleGrantedAuthority(RolesType.ROLE_GUARD.name()));

        mockMvc.perform(get("/vehicle").with(guard))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence").with(guard))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence/00000000-0000-0000-0000-000000000001").with(guard))
                .andExpect(status().isOk());
        mockMvc.perform(post("/occurrence/warning").with(guard))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/notification/id").with(guard))
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle").with(guard))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/park/profile").with(guard))
                .andExpect(status().isForbidden());
    }

    @Test
    void restrictsRhToRhAndNotificationResources() throws Exception {
        var rh = user("rh").authorities(new SimpleGrantedAuthority(RolesType.ROLE_RH.name()));

        mockMvc.perform(get("/rh").with(rh))
                .andExpect(status().isOk());
        mockMvc.perform(get("/notification").with(rh))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence").with(rh))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence/00000000-0000-0000-0000-000000000001").with(rh))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence/me").with(rh))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/vehicle").with(rh))
                .andExpect(status().isForbidden());
    }

    @Test
    void permitsAdminToAccessEveryEndpointExceptAdminBootstrap() throws Exception {
        var admin = user("admin").authorities(new SimpleGrantedAuthority(RolesType.ROLE_ADMIN.name()));

        mockMvc.perform(post("/auth/admin").with(admin))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/rh").with(admin))
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle").with(admin))
                .andExpect(status().isOk());
        mockMvc.perform(post("/occurrence/warning").with(admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/occurrence/00000000-0000-0000-0000-000000000001").with(admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/notification").with(admin))
                .andExpect(status().isOk());
        mockMvc.perform(get("/admin-fallback").with(admin))
                .andExpect(status().isOk());
    }

    @Test
    void blocksProtectedAndUnlistedEndpoints() throws Exception {
        mockMvc.perform(get("/vehicle"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/admin-fallback")
                        .with(user("park").authorities(new SimpleGrantedAuthority(RolesType.ROLE_PARK.name()))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/auth/admin"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class SecurityTestConfig {

        @Bean
        AuthenticationEntryPoint authenticationEntryPoint() {
            return (request, response, exception) -> response.sendError(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @RestController
    static class SecurityEndpoints {

        @PostMapping({
                "/auth/login",
                "/auth/register/visitor",
                "/auth/reset-password/check-email",
                "/auth/admin",
                "/vehicle",
                "/occurrence/warning"
        })
        void postEndpoint() {
        }

        @PatchMapping("/auth/reset-password")
        void patchEndpoint() {
        }

        @GetMapping({
                "/auth/validate-email/{token}",
                "/vehicle",
                "/vehicle/me",
                "/occurrence",
                "/occurrence/me",
                "/occurrence/{uuid}",
                "/notification",
                "/park/profile",
                "/rh",
                "/admin-fallback"
        })
        void getEndpoint() {
        }

        @DeleteMapping("/notification/{uuid}")
        void deleteEndpoint() {
        }

        @PutMapping({"/vehicle/{uuid}", "/occurrence/warning/{uuid}"})
        void putEndpoint() {
        }
    }
}

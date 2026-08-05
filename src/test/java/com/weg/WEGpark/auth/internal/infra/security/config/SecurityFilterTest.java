package com.weg.WEGpark.auth.internal.infra.security.config;

import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SecurityFilterTest {

    private TokenConfig tokenConfig;
    private UserRepository userRepository;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        tokenConfig = mock(TokenConfig.class);
        userRepository = mock(UserRepository.class);
        authenticationEntryPoint = mock(AuthenticationEntryPoint.class);
        securityFilter = new SecurityFilter(tokenConfig, userRepository, authenticationEntryPoint);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticatesActiveUserEvenWhenEmailValidationIsFalse() throws Exception {
        UUID userUuid = UUID.randomUUID();
        JWTUserData userData = new JWTUserData(
                userUuid, "active@weg.net", List.of("ROLE_PARK"), "Active User"
        );
        User user = new User("active@weg.net", "encoded");
        user.setActive(true);
        user.setEmailValidated(false);
        when(tokenConfig.validateToken("token")).thenReturn(Optional.of(userData));
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        MockHttpServletRequest request = requestWithToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        securityFilter.doFilter(request, response, filterChain);

        assertSame(userData, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
        verify(authenticationEntryPoint, never()).commence(any(), any(), any());
    }

    @Test
    void rejectsTokenWhenUserWasDeactivated() throws Exception {
        UUID userUuid = UUID.randomUUID();
        JWTUserData userData = new JWTUserData(
                userUuid, "inactive@weg.net", List.of("ROLE_PARK"), "Inactive User"
        );
        User user = new User("inactive@weg.net", "encoded");
        user.setActive(false);
        user.setEmailValidated(true);
        when(tokenConfig.validateToken("token")).thenReturn(Optional.of(userData));
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        MockHttpServletRequest request = requestWithToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        securityFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commence(any(), any(), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void rejectsTokenWhenAuthUserNoLongerExists() throws Exception {
        UUID userUuid = UUID.randomUUID();
        JWTUserData userData = new JWTUserData(
                userUuid, "missing@weg.net", List.of("ROLE_PARK"), "Missing User"
        );
        when(tokenConfig.validateToken("token")).thenReturn(Optional.of(userData));
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.empty());
        MockHttpServletRequest request = requestWithToken();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        securityFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(authenticationEntryPoint).commence(any(), any(), any());
        verify(filterChain, never()).doFilter(any(), any());
    }

    private MockHttpServletRequest requestWithToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        return request;
    }
}

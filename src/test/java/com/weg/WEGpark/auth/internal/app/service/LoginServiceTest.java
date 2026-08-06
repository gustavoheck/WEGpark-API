package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.InvalidLoginException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidRequestException;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.login.LoginRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.TokenConfig;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private TokenConfig tokenConfig;
    private AuthNotificationService notificationService;
    private AuthUserService authUserService;
    private LoginService service;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        tokenConfig = mock(TokenConfig.class);
        notificationService = mock(AuthNotificationService.class);
        authUserService = mock(AuthUserService.class);
        service = new LoginService(userRepository, authenticationManager, tokenConfig, notificationService, authUserService);
        user = new User("user@weg.net", "password");
        user.setId(5L);
        user.setEmailValidated(true);
        user.setRole(new Role(RolesType.ROLE_PARK));
    }

    @Test
    void listsRolesForKnownEmailAndRejectsUnknownOne() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(List.of(user));
        assertEquals(List.of(RolesType.ROLE_PARK), service.preLogin(user.getEmail()).stream().map(r -> r.role()).toList());

        when(userRepository.findByEmail("missing@weg.net")).thenReturn(List.of());
        assertThrows(InvalidLoginException.class, () -> service.preLogin("missing@weg.net"));
    }

    @Test
    void authenticatesValidatedAccountAndReturnsJwt() {
        LoginRequestDTO request = new LoginRequestDTO(user.getEmail(), "password", RolesType.ROLE_PARK.name());
        Authentication authentication = mock(Authentication.class);
        when(userRepository.findByEmailAndRole_Role(user.getEmail(), RolesType.ROLE_PARK)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authUserService.getUserName(user)).thenReturn("User");
        when(tokenConfig.generateToken(user, "User")).thenReturn("jwt");

        var response = service.login(request);

        assertAll(() -> assertTrue(response.authenticated()), () -> assertEquals("jwt", response.token()));
    }

    @Test
    void resendsValidationForUnvalidatedAccount() {
        user.setEmailValidated(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(List.of(user));

        var response = service.login(new LoginRequestDTO(user.getEmail(), "password", null));

        assertFalse(response.authenticated());
        assertNull(response.token());
        verify(notificationService).sendAccountEmailValidation(user);
    }

    @Test
    void convertsAuthenticationFailureToInvalidLogin() {
        when(userRepository.findByEmailAndRole_Role(user.getEmail(), RolesType.ROLE_PARK)).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));

        assertThrows(InvalidLoginException.class,
                () -> service.login(new LoginRequestDTO(user.getEmail(), "wrong", RolesType.ROLE_PARK.name())));
    }

    @Test
    void rejectsInvalidRoleValue() {
        LoginRequestDTO request = new LoginRequestDTO(user.getEmail(), "password", "INVALID_ROLE");

        assertThrows(InvalidRequestException.class, () -> service.login(request));

        verifyNoInteractions(userRepository, authenticationManager, tokenConfig, notificationService, authUserService);
    }
}

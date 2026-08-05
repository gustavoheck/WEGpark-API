package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.SendAccountValidationEmailEvent;
import com.weg.WEGpark.auth.SendEmailCheckEvent;
import com.weg.WEGpark.auth.internal.app.exception.InvalidEmailValidationException;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.NumberToken;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRequestDTO;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRoleRequestDTO;
import com.weg.WEGpark.auth.internal.dto.reset.NewTokenResponseDTO;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.exception.InvalidTokenException;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthNotificationServiceTest {
    private ApplicationEventPublisher publisher;
    private AuthTokenService tokenService;
    private UserRepository userRepository;
    private AuthNotificationService service;
    private User user;

    @BeforeEach
    void setUp() {
        publisher = mock(ApplicationEventPublisher.class);
        tokenService = mock(AuthTokenService.class);
        userRepository = mock(UserRepository.class);
        service = new AuthNotificationService(publisher, tokenService, userRepository);
        user = new User("user@weg.net", "password");
        user.setRole(new Role(RolesType.ROLE_PARK));
    }

    @Test
    void publishesEmailValidationEvent() {
        AuthToken token = new AuthToken(TokenType.EMAIL_VALIDATION, user, 15);
        UUID id = UUID.randomUUID();
        token.setToken(id);
        when(tokenService.createAuthToken(user, TokenType.EMAIL_VALIDATION)).thenReturn(token);

        service.sendAccountEmailValidation(user);

        verify(publisher).publishEvent(new SendAccountValidationEmailEvent(id, user.getEmail(), TokenType.EMAIL_VALIDATION));
    }

    @Test
    void resendsEmailValidationForEveryPendingAccount() {
        User guardUser = new User(user.getEmail(), "password");
        guardUser.setRole(new Role(RolesType.ROLE_GUARD));
        user.setEmailValidated(false);
        guardUser.setEmailValidated(false);

        AuthToken parkToken = new AuthToken(TokenType.EMAIL_VALIDATION, user, 15);
        parkToken.setToken(UUID.randomUUID());
        AuthToken guardToken = new AuthToken(TokenType.EMAIL_VALIDATION, guardUser, 15);
        guardToken.setToken(UUID.randomUUID());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(List.of(user, guardUser));
        when(tokenService.createAuthToken(user, TokenType.EMAIL_VALIDATION)).thenReturn(parkToken);
        when(tokenService.createAuthToken(guardUser, TokenType.EMAIL_VALIDATION)).thenReturn(guardToken);

        service.resendAccountEmailValidation(new EmailRequestDTO(user.getEmail()));

        verify(publisher).publishEvent(new SendAccountValidationEmailEvent(
                parkToken.getToken(), user.getEmail(), TokenType.EMAIL_VALIDATION));
        verify(publisher).publishEvent(new SendAccountValidationEmailEvent(
                guardToken.getToken(), guardUser.getEmail(), TokenType.EMAIL_VALIDATION));
    }

    @Test
    void rejectsEmailValidationResendForUnknownUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(List.of());

        assertThrows(NotFoundException.class,
                () -> service.resendAccountEmailValidation(new EmailRequestDTO(user.getEmail())));

        verifyNoInteractions(tokenService, publisher);
    }

    @Test
    void rejectsEmailValidationResendWhenEveryAccountIsAlreadyValidated() {
        user.setEmailValidated(true);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(List.of(user));

        assertThrows(InvalidEmailValidationException.class,
                () -> service.resendAccountEmailValidation(new EmailRequestDTO(user.getEmail())));

        verifyNoInteractions(tokenService, publisher);
    }

    @Test
    void sendsPasswordResetCodeForExistingRole() {
        NumberToken token = new NumberToken("123456", user);
        UUID id = UUID.randomUUID();
        token.setIdentificationToken(id);
        when(userRepository.findByEmailAndRole_Role(user.getEmail(), RolesType.ROLE_PARK)).thenReturn(Optional.of(user));
        when(tokenService.createNumberToken(user)).thenReturn(token);

        NewTokenResponseDTO response = service.resetPasswordEmailCheck(new EmailRoleRequestDTO(user.getEmail(), RolesType.ROLE_PARK.name()));

        assertEquals(id, response.token());
        verify(publisher).publishEvent(new SendEmailCheckEvent(user.getEmail(), "123456"));
    }

    @Test
    void rejectsPasswordResetForUnknownUser() {
        when(userRepository.findByEmailAndRole_Role(any(), any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.resetPasswordEmailCheck(new EmailRoleRequestDTO("missing@weg.net", RolesType.ROLE_PARK.name())));
    }

    @Test
    void validatesUnusedNonExpiredAccountToken() {
        AuthToken token = new AuthToken(TokenType.EMAIL_VALIDATION, user, 15);
        user.setEmailValidated(false);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(1));
        UUID tokenId = UUID.randomUUID();
        when(tokenService.findToken(tokenId)).thenReturn(token);

        service.validateAccount(tokenId);

        assertTrue(user.getEmailValidated());
        assertTrue(user.getActive());
        assertTrue(token.getUsed());
    }

    @Test
    void rejectsUsedOrAlreadyValidatedAccountToken() {
        AuthToken token = new AuthToken(TokenType.EMAIL_VALIDATION, user, 15);
        user.setEmailValidated(false);
        token.setUsed(true);
        UUID tokenId = UUID.randomUUID();
        when(tokenService.findToken(tokenId)).thenReturn(token);
        assertThrows(InvalidTokenException.class, () -> service.validateAccount(tokenId));

        user.setEmailValidated(true);
        assertThrows(InvalidEmailValidationException.class, () -> service.validateAccount(tokenId));
    }
}

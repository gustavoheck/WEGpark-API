package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.app.exception.InvalidRequestException;
import com.weg.WEGpark.auth.internal.app.dto.UpdateUserDTO;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.exception.InvalidTokenException;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthUpdateServiceTest {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthTokenService tokenService;
    private UserMapper mapper;
    private AuthUpdateService service;
    private User user;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        userRepository = mock(UserRepository.class);
        tokenService = mock(AuthTokenService.class);
        mapper = mock(UserMapper.class);
        service = new AuthUpdateService(passwordEncoder, userRepository, tokenService, mapper);
        user = new User("old@weg.net", "encoded-old");
        user.setId(7L);
        user.setRole(new Role(RolesType.ROLE_PARK));
    }

    @Test
    void updatesCredentialsWhenCurrentPasswordMatches() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("old@weg.net", RolesType.ROLE_PARK.name(), "new-password", "old-password", null);
        UpdateUserResponseDTO response = new UpdateUserResponseDTO(7L, UUID.randomUUID(), "new@weg.net");
        when(userRepository.findByEmailAndRole_Role("old@weg.net", RolesType.ROLE_PARK)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
        when(mapper.toUpdateDto(request)).thenReturn(new UpdateUserDTO("new@weg.net", "new-password"));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");
        when(mapper.toUpdateResponse(user)).thenReturn(response);

        assertSame(response, service.updateUserAuthDataRequest(request));
        assertEquals("encoded-new", user.getPassword());
        verify(mapper).updateFromDTO(any(UpdateUserDTO.class), same(user));
        verify(userRepository).save(user);
    }

    @Test
    void rejectsIncorrectCurrentPassword() {
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("old@weg.net", RolesType.ROLE_PARK.name(), null, "wrong", null);
        when(userRepository.findByEmailAndRole_Role(anyString(), any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.updateUserAuthDataRequest(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updatesCredentialsUsingValidPasswordResetToken() {
        UUID tokenId = UUID.randomUUID();
        AuthToken token = new AuthToken(TokenType.PASSWORD_RESET, user, 15);
        token.setExpirationTime(LocalDateTime.now().plusMinutes(1));
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("ignored@weg.net", RolesType.ROLE_PARK.name(), "new-password", null, tokenId.toString());
        when(tokenService.findToken(tokenId)).thenReturn(token);
        when(mapper.toUpdateDto(request)).thenReturn(new UpdateUserDTO("new@weg.net", "new-password"));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");
        when(mapper.toUpdateResponse(user)).thenReturn(new UpdateUserResponseDTO(7L, UUID.randomUUID(), "new@weg.net"));

        service.updateUserAuthDataRequest(request);

        assertTrue(token.getUsed());
        verify(userRepository).save(user);
    }

    @Test
    void rejectsExpiredOrWrongPurposeResetToken() {
        UUID tokenId = UUID.randomUUID();
        AuthToken token = new AuthToken(TokenType.PASSWORD_RESET, user, 15);
        token.setExpirationTime(LocalDateTime.now().minusSeconds(1));
        when(tokenService.findToken(tokenId)).thenReturn(token);
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("x@weg.net", RolesType.ROLE_PARK.name(), "new-password", null, tokenId.toString());

        assertThrows(InvalidTokenException.class, () -> service.updateUserAuthDataRequest(request));
    }

    @Test
    void updatesAndCompletesEventResponseOrCompletesItExceptionally() {
        UUID uuid = UUID.randomUUID();
        CompletableFuture<UpdateUserResponseDTO> success = new CompletableFuture<>();
        UpdateUserAuthEvent event = new UpdateUserAuthEvent(success, uuid, "new@weg.net", "new-password");
        UpdateUserResponseDTO response = new UpdateUserResponseDTO(7L, uuid, "new@weg.net");
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        when(mapper.toUpdateResponse(user)).thenReturn(response);
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-new");

        service.updateUserAuthDataEvent(event);

        assertSame(response, success.join());
        verify(userRepository).save(user);

        CompletableFuture<UpdateUserResponseDTO> missing = new CompletableFuture<>();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        service.updateUserAuthDataEvent(new UpdateUserAuthEvent(missing, uuid, null, null));
        assertTrue(missing.isCompletedExceptionally());
    }

    @Test
    void togglesActivationAndReturnsTargetId() {
        UUID uuid = UUID.randomUUID();
        user.setActive(true);
        CompletableFuture<Long> response = new CompletableFuture<>();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));

        service.activateAndDesactivateUser(new DesactivateAndActivateUserEvent(response, uuid));

        assertEquals(7L, response.join());
        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }

    @Test
    void rejectsActivationForUnknownUser() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> service.activateAndDesactivateUser(new DesactivateAndActivateUserEvent(new CompletableFuture<>(), uuid)));
    }

    @Test
    void rejectsInvalidRoleAndPasswordResetUuid() {
        UpdateUserRequestDTO invalidRole = new UpdateUserRequestDTO(
                "old@weg.net", "INVALID_ROLE", "new-password", "old-password", null);
        UpdateUserRequestDTO invalidUuid = new UpdateUserRequestDTO(
                null, null, "new-password", null, "invalid-uuid");

        assertThrows(InvalidRequestException.class, () -> service.updateUserAuthDataRequest(invalidRole));
        assertThrows(InvalidRequestException.class, () -> service.updateUserAuthDataRequest(invalidUuid));

        verifyNoInteractions(passwordEncoder, userRepository, tokenService, mapper);
    }
}

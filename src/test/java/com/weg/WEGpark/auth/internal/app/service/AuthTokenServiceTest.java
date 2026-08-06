package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.InvalidRequestException;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.NumberToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.reset.NumberTokenVerificateTryRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.AuthTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.NumberTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthTokenServiceTest {
    private UserRepository userRepository;
    private AuthTokenRepository authTokenRepository;
    private NumberTokenRepository numberTokenRepository;
    private AuthTokenService service;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authTokenRepository = mock(AuthTokenRepository.class);
        numberTokenRepository = mock(NumberTokenRepository.class);
        service = new AuthTokenService(userRepository, authTokenRepository, numberTokenRepository);
        user = new User("user@weg.net", "password");
        user.setId(10L);
    }

    @Test
    void createsAuthenticationTokenForPersistedUser() {
        when(userRepository.existsById(10L)).thenReturn(true);

        AuthToken token = service.createAuthToken(user, TokenType.EMAIL_VALIDATION);

        assertAll(
                () -> assertEquals(TokenType.EMAIL_VALIDATION, token.getTokenType()),
                () -> assertEquals(user, token.getTargetUser()),
                () -> assertFalse(token.getUsed())
        );
        verify(authTokenRepository).save(token);
    }

    @Test
    void rejectsTokenCreationForUnknownUser() {
        when(userRepository.existsById(10L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.createAuthToken(user, TokenType.PASSWORD_RESET));
        verifyNoInteractions(authTokenRepository);
    }

    @Test
    void createsSixDigitNumberTokenForPersistedUser() {
        when(userRepository.existsById(10L)).thenReturn(true);

        NumberToken token = service.createNumberToken(user);

        assertTrue(token.getDigits().matches("\\d{6}"));
        assertEquals(user, token.getTargetUser());
        verify(numberTokenRepository).save(token);
    }

    @Test
    void validatesCorrectNumberTokenAndCreatesPasswordResetToken() {
        UUID numberTokenId = UUID.randomUUID();
        NumberToken numberToken = new NumberToken("123456", user);
        numberToken.setIdentificationToken(numberTokenId);
        numberToken.setExpirationTime(LocalDateTime.now().plusMinutes(5));
        when(numberTokenRepository.findById(numberTokenId)).thenReturn(Optional.of(numberToken));
        when(userRepository.existsById(10L)).thenReturn(true);
        doAnswer(invocation -> {
            AuthToken saved = invocation.getArgument(0);
            saved.setToken(UUID.randomUUID());
            return saved;
        }).when(authTokenRepository).save(any(AuthToken.class));

        var response = service.validateNumberToken(new NumberTokenVerificateTryRequestDTO(numberTokenId.toString(), "123456"));

        assertNotNull(response.token());
        assertTrue(numberToken.getUsed());
        verify(authTokenRepository).save(any(AuthToken.class));
    }

    @Test
    void findsTokensOrRaisesNotFound() {
        UUID tokenId = UUID.randomUUID();
        AuthToken authToken = new AuthToken(TokenType.PASSWORD_RESET, user, 15);
        when(authTokenRepository.findById(tokenId)).thenReturn(Optional.of(authToken));
        when(numberTokenRepository.findById(tokenId)).thenReturn(Optional.empty());

        assertSame(authToken, service.findToken(tokenId));
        assertThrows(NotFoundException.class, () -> service.findNumberToken(tokenId));
    }

    @Test
    void rejectsInvalidNumberTokenUuid() {
        NumberTokenVerificateTryRequestDTO request = new NumberTokenVerificateTryRequestDTO("invalid-uuid", "123456");

        assertThrows(InvalidRequestException.class, () -> service.validateNumberToken(request));

        verifyNoInteractions(userRepository, authTokenRepository, numberTokenRepository);
    }
}

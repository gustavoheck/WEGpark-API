package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.InvalidTokenException;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.NumberToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.reset.ResetPasswordEmailCheckRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.AuthTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.NumberTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthTokenService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;
    private final NumberTokenRepository numberTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public AuthToken createAuthToken (User user, TokenType tokenType) {
        if (userRepository.existsById(user.getId())) {
            AuthToken authToken = new AuthToken(tokenType,user, 15);
            authTokenRepository.save(authToken);
            return authToken;
        }
        throw new NotFoundException("Any user was found by %s id to create a token".formatted(user.getId()));
    }

    public NumberToken createNumberToken (User user) {
        if (userRepository.existsById(user.getId())) {
            String numberCode = String.format("%06d", secureRandom.nextInt(1_000_000));
            NumberToken numberToken = new NumberToken(numberCode, user);

            numberTokenRepository.save(numberToken);
            return numberToken;
        }
        throw new NotFoundException("Any user was found by %s id to create a token".formatted(user.getId()));
    }

    @Transactional
    public AuthToken validateNumberToken (ResetPasswordEmailCheckRequestDTO request) {
        NumberToken numberToken = findNumberToken(UUID.fromString(request.token()));

            if (!numberToken.getUsed() && numberToken.getExpirationTime().isAfter(LocalDateTime.now()) && numberToken.getTries() < 5) {
                if (numberToken.getDigits().equals(request.numberCode())) {
                    numberToken.setUsed(true);
                    return createAuthToken(numberToken.getTargetUser(), TokenType.PASSWORD_RESET);
                } else {
                    throw new InvalidTokenException("Request digits don't match with token digits");
                }
            } else {
                throw new InvalidTokenException("This token is already used or expired");
            }
    }

    public AuthToken findToken (UUID token) {
        return authTokenRepository.findById(token)
                .orElseThrow(() -> new NotFoundException("Any %s token was found in system".formatted(token)));
    }

    public NumberToken findNumberToken (UUID token) {
        return numberTokenRepository.findById(token)
                .orElseThrow(() -> new NotFoundException("Any %s token was found in system".formatted(token)));
    }
}

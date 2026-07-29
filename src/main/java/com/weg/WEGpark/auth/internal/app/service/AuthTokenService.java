package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.domain.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.AuthTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthTokenService {

    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    @Transactional
    public void createToken (User user, TokenType tokenType) {
        if (userRepository.existsById(user.getId())) {
            authTokenRepository.save(
                    new AuthToken(
                            tokenType,
                            user
                    )
            );
        }
        throw new NotFoundException("Any user was found by %s id to create a token".formatted(user));
    }

    public AuthToken findToken (UUID token) {
        AuthToken authToken = authTokenRepository.findById(token)
                .orElseThrow(() -> new NotFoundException("Any %s token was found in system".formatted(token)));

        return authToken;
    }
}

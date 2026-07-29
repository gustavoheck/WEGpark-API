package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.app.exception.InvalidEmailValidationException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidTokenException;
import com.weg.WEGpark.auth.internal.domain.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthNotificationService {

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    @Transactional
    public void sendAccountEmailValidation (String email, RolesType role) {
        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> new NotFoundException("Any %s user was found by %s email".formatted(role, email)));

        authTokenService.createToken(user, TokenType.EMAIL_VALIDATION);
    }

    @Transactional
    public void validateAccount (UUID token) {
        AuthToken authToken = authTokenService.findToken(token);

        if (authToken.getTargetUser().getEmailValidated()) {
            if (authToken.getUsed() || authToken.getExpirationTime().isAfter(LocalDateTime.now())) {
                authToken.getTargetUser().setEmailValidated(true);
                authToken.getTargetUser().setActive(true);
                authToken.setUsed(true);
            }
            throw new InvalidTokenException("This validation token is already used or expired");
        }
        throw new InvalidEmailValidationException("Your account email is already validated");
    }
}

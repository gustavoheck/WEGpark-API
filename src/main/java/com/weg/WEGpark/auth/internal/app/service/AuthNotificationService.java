package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.SendAccountValidationEmailEvent;
import com.weg.WEGpark.auth.SendEmailCheckEvent;
import com.weg.WEGpark.auth.internal.app.exception.InvalidEmailValidationException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidTokenException;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.NumberToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.defaults.EmailRequestDTO;
import com.weg.WEGpark.auth.internal.dto.reset.ResetPasswordEmailCheckResponseDTO;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class AuthNotificationService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;

    @Transactional
    public void sendAccountEmailValidation (User user) {
        AuthToken authToken = authTokenService.createAuthToken(user, TokenType.EMAIL_VALIDATION);
        applicationEventPublisher.publishEvent(new SendAccountValidationEmailEvent(authToken.getToken(), user.getEmail(), TokenType.EMAIL_VALIDATION));
    }

    @Transactional
    public ResetPasswordEmailCheckResponseDTO resetPasswordEmailCheck (EmailRequestDTO request) {
        User user = userRepository.findByEmailAndRole_Role(request.email(), RolesType.valueOf(request.role()))
                .orElseThrow(() -> new NotFoundException("Any user was found by %s email and %s role".formatted(request.email(), request.role())));

        NumberToken numberToken = authTokenService.createNumberToken(user);
        applicationEventPublisher.publishEvent(new SendEmailCheckEvent(numberToken.getDigits()));
        return new ResetPasswordEmailCheckResponseDTO(numberToken.getIdentificationToken());
    }

    @Transactional
    public void validateAccount (UUID token) {
        AuthToken authToken = authTokenService.findToken(token);

        if (!authToken.getTargetUser().getEmailValidated()) {
            if (!authToken.getUsed() && authToken.getExpirationTime().isAfter(LocalDateTime.now())) {
                authToken.getTargetUser().setEmailValidated(true);
                authToken.getTargetUser().setActive(true);
                authToken.setUsed(true);
            } else {
                throw new InvalidTokenException("This token is already used or expired");
            }
        } else {
            throw new InvalidEmailValidationException("Your account email is already validated");
        }
    }
}

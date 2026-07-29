package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.domain.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    @Transactional
    public void sendAccountEmailValidation (String email, RolesType role) {
        User user = userRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> new NotFoundException("Any %s user was found by %s email".formatted(role, email)));

        authTokenService.createToken(user, TokenType.EMAIL_VALIDATION);


    }
}

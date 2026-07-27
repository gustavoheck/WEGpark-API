package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.app.exception.InvalidTokenException;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.internal.domain.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.internal.infra.repository.AuthTokenRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UpdateService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthTokenRepository authTokenRepository;

    private final UserMapper userMapper;

    @Transactional
    public UpdateUserResponseDTO updateUserAuthDataRequest (UpdateUserRequestDTO request, UUID uuid, boolean isPasswordMiss) {
        User user;
        if (isPasswordMiss) {
            user = checkToken(uuid, TokenType.PASSWORD_RESET);
        } else {
            user = userRepository.findByUuid(uuid)
                    .orElseThrow(() -> new NotFoundException("Any user was found by %s uuid".formatted(uuid)));
        }
        boolean isPasswordCorrect = passwordEncoder.matches(request.actualPassword(), user.getPassword());
        if (isPasswordCorrect) {
            userMapper.updateFromDTO(request, user);
            if (request.password() != null) {
                user.setPassword(passwordEncoder.encode(request.password()));
            }
            userRepository.save(user);
            return userMapper.toUpdateResponse(user);
        }
        throw new BadCredentialsException("Password is Wrong");
    }

    @Transactional
    public void updateUserAuthDataEvent (UpdateUserAuthEvent event) {
        User user = userRepository.findByUuid(event.targetUuid())
                .orElseThrow(() -> new NotFoundException("Any user was found by %s uuid".formatted(event.targetUuid())));

        userMapper.updateFromEvent(event, user);
        if (event.password() != null) {
            user.setPassword(passwordEncoder.encode(event.password()));
        }
        userRepository.save(user);
        event.eventResponse().complete(userMapper.toUpdateResponse(user));
    }

    @Transactional
    private User checkToken (UUID uuid, TokenType operation) {
        AuthToken token = authTokenRepository.findById(uuid)
                .orElseThrow(() -> new NotFoundException("This uuid dont correspond to any token"));
        if (LocalDateTime.now().isAfter(token.getExpirationTime())) throw new InvalidTokenException("This token is already expired");
        if (token.getUsed()) throw new InvalidTokenException("This token is already used");
        if (!(token.getTokenType().equals(operation.toString()))) throw new InvalidTokenException("You can not use a token of %s to %s"
                .formatted(token.getTokenType(), operation));
        return token.getTargetUser();
    }
}

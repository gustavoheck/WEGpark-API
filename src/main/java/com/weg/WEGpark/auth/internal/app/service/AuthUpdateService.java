package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.app.exception.InvalidTokenException;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.shared.enums.TokenType;
import com.weg.WEGpark.auth.internal.domain.model.AuthToken;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.rh.DesactivateAndActivateUserEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUpdateService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthTokenService authTokenService;

    private final UserMapper userMapper;

    @Transactional
    public UpdateUserResponseDTO updateUserAuthDataRequest (UpdateUserRequestDTO request) {
        User user;
        if (request.tokenIfPasswordReset() != null) {
            AuthToken token = checkToken(UUID.fromString(request.tokenIfPasswordReset()), TokenType.PASSWORD_RESET);
            user = token.getTargetUser();
            token.setUsed(true);
        } else {
            user = userRepository.findByEmailAndRole_Role(request.email(), RolesType.valueOf(request.role()))
                    .orElseThrow(() -> new NotFoundException("Any %s user was found by %s email".formatted(request.role(), request.email())));
        }
        boolean isPasswordCorrect = passwordEncoder.matches(request.actualPassword(), user.getPassword());
        if (isPasswordCorrect) {
            userMapper.updateFromDTO(userMapper.toUpdateDto(request), user);
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
        Optional<User> optUser = userRepository.findByUuid(event.targetUuid());

        if (optUser.isPresent()) {
            User user = optUser.get();
            userMapper.updateFromEvent(event, user);
            if (event.password() != null) {
                user.setPassword(passwordEncoder.encode(event.password()));
            }
            userRepository.save(user);
            event.eventResponse().complete(userMapper.toUpdateResponse(user));
        } else {
            event.eventResponse().completeExceptionally(new NotFoundException("Any user was found by %s uuid".formatted(event.targetUuid())));
        }
    }

    @Transactional
    public void activateAndDesactivateUser (DesactivateAndActivateUserEvent event) {
        User user = userRepository.findByUuid(event.uuid())
                .orElseThrow(() -> new NotFoundException("Any user was found by %s uuid".formatted(event.uuid())));

        user.setActive(!user.getActive());

        userRepository.save(user);
    }

    @Transactional
    private AuthToken checkToken (UUID uuid, TokenType operation) {
        AuthToken token = authTokenService.findToken(uuid);
        if (LocalDateTime.now().isAfter(token.getExpirationTime())) throw new InvalidTokenException("This token is already expired");
        if (token.getUsed()) throw new InvalidTokenException("This token is already used");
        if (!(token.getTokenType().toString().equals(operation.toString()))) throw new InvalidTokenException("You can not use a token of %s to %s"
                .formatted(token.getTokenType(), operation));
        return token;
    }
}

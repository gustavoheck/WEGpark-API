package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.ValidateBadgeNumberEvent;
import com.weg.WEGpark.auth.ValidateVisitorEmailEvent;
import com.weg.WEGpark.auth.internal.app.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.auth.internal.app.mapper.EventMapper;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.internal.domain.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.SecurityConfig;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig securityConfig;

    private final EventMapper eventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RegisterAccountResponseDTO registerCollaborator (
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            RegisterCollaboratorRequestDTO request,
            Long collaboratorId) {
        boolean canRegister;
        if (collaboratorId != null ) {
            User user = userRepository.findById(collaboratorId).get();
            if (user.getRole().getRole().equals(RolesType.PARK)) {
                canRegister = false;
            } else {
                canRegister = true;
            }
        } else {
            canRegister = true;
        }
        if (canRegister) {
            RegisterAccountResponseDTO response = registerParkAccount(request.defaults(), futureResponse);
            applicationEventPublisher.publishEvent(eventMapper.toCollaboratorRegisteredEvent(request, futureResponse));
            return response;
        }
        futureResponse.completeExceptionally(new AlreadyHaveAccountException("An account with this badge number is already registered!"));
        return null;
    }

    @Transactional
    public RegisterAccountResponseDTO registerVisitor (
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            RegisterVisitorRequestDTO request,
            Boolean alreadyExists
    ) {
        if (!alreadyExists) {
            RegisterAccountResponseDTO response = registerParkAccount(request.defaults(), futureResponse);
            applicationEventPublisher.publishEvent(eventMapper.toVisitorRegisteredEvent(request, futureResponse));
            return response;
        }
        futureResponse.completeExceptionally(new AlreadyHaveAccountException("An account with this email is already registered!"));
        return null;
    }

    @Transactional
    private RegisterAccountResponseDTO registerParkAccount (
            RegisterAccountRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse) {
        User user = userMapper.toEntity(request);

        Optional<Role> role = roleRepository.findByRole(RolesType.PARK);

        if (role.isEmpty()) {
            futureResponse.completeExceptionally(new NotFoundException("Any PARK role was found"));
        }

        user.setRole(role.get());
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Transactional
    public CompletableFuture<RegisterAccountResponseDTO> checkVisitorAccountsBeforeRegistering
            (RegisterVisitorRequestDTO request)
    {
        CompletableFuture<RegisterAccountResponseDTO> futureResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new ValidateVisitorEmailEvent(futureResponse, request));
        return futureResponse;
    }

    @Transactional
    public CompletableFuture<RegisterAccountResponseDTO> checkBadgeNumberBeforeRegistering
            (RegisterCollaboratorRequestDTO request)
    {
        CompletableFuture<RegisterAccountResponseDTO> futureResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new ValidateBadgeNumberEvent(futureResponse, request));
        return futureResponse;
    }
}

package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.ValidateCollaboratorByEvent;
import com.weg.WEGpark.auth.ValidateCollaboratorEvent;
import com.weg.WEGpark.auth.ValidateVisitorEvent;
import com.weg.WEGpark.auth.shared.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.auth.internal.app.mapper.AuthEventMapper;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.SecurityConfig;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.RegisterRhEvent;
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

    private final AuthEventMapper authEventMapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AuthNotificationService authNotificationService;

    @Transactional
    public void registerCollaborator (
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            RegisterCollaboratorRequestDTO request,
            Long collaboratorId) {
        if (!verifyCollaboratorRegistering(collaboratorId, RolesType.ROLE_PARK)) {
            futureResponse.completeExceptionally(new AlreadyHaveAccountException("An account with this badge number or email is already registered!"));
        } else {
            registerParkAccount(request.defaults(), futureResponse)
                    .ifPresent(user -> applicationEventPublisher.publishEvent(
                            authEventMapper.toCollaboratorRegisteredEvent(request, futureResponse, user)
                    ));
        }
    }

    @Transactional
    public void registerGuard (RegisterGuardEvent event, Long collaboratorId) {
        if (!verifyCollaboratorRegistering(collaboratorId, RolesType.ROLE_GUARD)) {
            event.registerResponse().completeExceptionally(new AlreadyHaveAccountException("An account with this badge number or email is already registered!"));
        } else {
            registerGuardAccount(event)
                    .ifPresent(user -> applicationEventPublisher.publishEvent(
                            authEventMapper.ToGuardRegisteredEvent(event, user)
                    ));
        }
    }

    private boolean verifyCollaboratorRegistering (Long collaboratorId, RolesType roleToCompare) {
        if (collaboratorId == null) {
            return true;
        }

        User user = userRepository.findById(collaboratorId)
                .orElseThrow(() -> new NotFoundException(
                        "No auth user was found by %s id".formatted(collaboratorId)
                ));

        return !user.getRole().getRole().equals(roleToCompare);
    }

    @Transactional
    public void registerVisitor (
            CompletableFuture<RegisterAccountResponseDTO> futureResponse,
            RegisterVisitorRequestDTO request,
            Boolean alreadyExists
    ) {
        if (alreadyExists) {
            futureResponse.completeExceptionally(new AlreadyHaveAccountException("An account with this email is already registered!"));
        } else {
            registerParkAccount(request.defaults(), futureResponse)
                    .ifPresent(user -> applicationEventPublisher.publishEvent(
                            authEventMapper.toVisitorRegisteredEvent(request, futureResponse, user)
                    ));
        }
    }

    @Transactional
    private Optional<User> registerParkAccount (
            RegisterAccountRequestDTO request,
            CompletableFuture<RegisterAccountResponseDTO> futureResponse) {

        Optional<Role> role = roleRepository.findByRole(RolesType.ROLE_PARK);

        if (role.isEmpty()) {
            futureResponse.completeExceptionally(new NotFoundException("Any PARK role was found"));
            return Optional.empty();
        }

        User user = userMapper.toEntity(request);
        return Optional.of(registerAccount(user, role.get()));
    }

    @Transactional
    private Optional<User> registerGuardAccount (RegisterGuardEvent event) {
        Optional<Role> role = roleRepository.findByRole(RolesType.ROLE_GUARD);
        if (role.isEmpty()) {
            event.registerResponse().completeExceptionally(new NotFoundException("Any GUARD role was found"));
            return Optional.empty();
        }

        User user = userMapper.toEntityFromGuardEvent(event);
        return Optional.of(registerAccount(user, role.get()));
    }

    @Transactional
    public void registerRhAccount (RegisterRhEvent event) {
        Optional<Role> role = roleRepository.findByRole(RolesType.ROLE_RH);
        if (role.isEmpty()) {
            event.eventResponse().completeExceptionally(new NotFoundException("Any RH role was found"));
        } else {
            User user = userMapper.toEntityFromRhEvent(event);
            registerAccount(user, role.get());
            event.eventResponse().complete(authEventMapper.toDefaultRegisteredEvent(user));
        }
    }

    @Transactional
    public RegisterAccountResponseDTO registerAdminAccount () {
        User user = new User(
                "admin@gmail.com",
                "admin"
        );
        Optional<Role> role = roleRepository.findByRole(RolesType.ROLE_ADMIN);
        if (role.isEmpty()) {
            throw new NotFoundException("Any Admin role was found");
        }
        registerAccount(user, role.get());
        return userMapper.toRegisterResponse(user);
    }

    @Transactional
    private User registerAccount (User user, Role role) {
        user.setRole(role);
        user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));
        if (role.getRole() != RolesType.ROLE_ADMIN) {
            user.setActive(false);
            user.setEmailValidated(false);
        } else {
            user.setActive(true);
            user.setEmailValidated(true);
        }
        userRepository.saveAndFlush(user);
        authNotificationService.sendAccountEmailValidation(user);
        return user;
    }

    @Transactional
    public CompletableFuture<RegisterAccountResponseDTO> checkVisitorAccountsBeforeRegistering
            (RegisterVisitorRequestDTO request)
    {
        CompletableFuture<RegisterAccountResponseDTO> futureResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new ValidateVisitorEvent(futureResponse, request));
        return futureResponse;
    }

    @Transactional
    public CompletableFuture<RegisterAccountResponseDTO> checkBadgeNumberBeforeRegistering
            (RegisterCollaboratorRequestDTO request)
    {
        CompletableFuture<RegisterAccountResponseDTO> futureResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new ValidateCollaboratorEvent(futureResponse, request));
        return futureResponse;
    }

    @Transactional
    public CompletableFuture<RegisterAccountResponseDTO> checkBadgeNumberBeforeRegisteringEvent
            (RegisterGuardEvent event)
    {
        CompletableFuture<RegisterAccountResponseDTO> futureResponse = new CompletableFuture<>();
        applicationEventPublisher.publishEvent(new ValidateCollaboratorByEvent(event));
        return futureResponse;
    }
}

package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.ValidateBadgeNumberEvent;
import com.weg.WEGpark.auth.ValidateVisitorEmailEvent;
import com.weg.WEGpark.auth.internal.app.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.internal.domain.enums.RolesType;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.internal.dto.visitor.RegisterVisitorRequestDTO;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.SecurityConfig;
import com.weg.WEGpark.auth.shared.dto.collaborator.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegisterService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SecurityConfig securityConfig;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public RegisterAccountResponseDTO registerCollaborator (RegisterCollaboratorRequestDTO request, Long collaboratorId) {
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
            User user = userMapper.toEntity(request.defaults());

            Role role = roleRepository.findByRole(RolesType.PARK.toString())
                    .orElseThrow(() -> new NotFoundException("Role PARK was not encountered"));

            user.setRole(role);
            user.setPassword(securityConfig.passwordEncoder().encode(user.getPassword()));

            userRepository.save(user);

            applicationEventPublisher.publishEvent(CollaboratorRegisteredEvent);

            return userMapper.toResponse(user);
        }
        throw new AlreadyHaveAccountException();
    }

    @Transactional
    private void checkVisitorAccountsBeforeRegistering (RegisterVisitorRequestDTO request) {
        applicationEventPublisher.publishEvent(new ValidateVisitorEmailEvent(request));
    }

    @Transactional
    private void checkCollaboratorBadgeNumberBeforeRegistering (RegisterCollaboratorRequestDTO request) {
        applicationEventPublisher.publishEvent(new ValidateBadgeNumberEvent(request));
    }
}

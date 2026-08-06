package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.ValidateCollaboratorByEvent;
import com.weg.WEGpark.auth.ValidateCollaboratorEvent;
import com.weg.WEGpark.auth.ValidateVisitorEvent;
import com.weg.WEGpark.auth.internal.app.mapper.AuthEventMapper;
import com.weg.WEGpark.auth.internal.app.mapper.UserMapper;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.RoleRepository;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.internal.infra.security.config.SecurityConfig;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.ParkGuardRegisteredEvent;
import com.weg.WEGpark.rh.RegisterGuardEvent;
import com.weg.WEGpark.rh.RegisterRhEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterServiceTest {
    private UserMapper userMapper;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private SecurityConfig securityConfig;
    private AuthEventMapper eventMapper;
    private ApplicationEventPublisher publisher;
    private AuthNotificationService notificationService;
    private RegisterService service;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userMapper = mock(UserMapper.class);
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        securityConfig = mock(SecurityConfig.class);
        eventMapper = mock(AuthEventMapper.class);
        publisher = mock(ApplicationEventPublisher.class);
        notificationService = mock(AuthNotificationService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        when(securityConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
        service = new RegisterService(userMapper, userRepository, roleRepository, securityConfig, eventMapper, publisher, notificationService);
    }

    @Test
    void registersVisitorParkAccountAndPublishesDomainEvent() {
        RegisterVisitorRequestDTO request = mock(RegisterVisitorRequestDTO.class);
        RegisterAccountRequestDTO defaults = new RegisterAccountRequestDTO("visitor@weg.net", "password");
        User user = user("visitor@weg.net", "password");
        CompletableFuture<RegisterAccountResponseDTO> future = new CompletableFuture<>();
        when(request.defaults()).thenReturn(defaults);
        when(userMapper.toEntity(defaults)).thenReturn(user);
        when(roleRepository.findByRole(RolesType.ROLE_PARK)).thenReturn(Optional.of(new Role(RolesType.ROLE_PARK)));
        when(eventMapper.toVisitorRegisteredEvent(eq(request), eq(future), eq(user))).thenReturn(mock(VisitorRegisteredEvent.class));

        service.registerVisitor(future, request, false);
        assertAll(() -> assertFalse(user.getActive()), () -> assertFalse(user.getEmailValidated()), () -> assertEquals("encoded-password", user.getPassword()));
        verify(userRepository).saveAndFlush(user);
        verify(notificationService).sendAccountEmailValidation(user);
        verify(publisher).publishEvent(any(Object.class));
    }

    @Test
    void rejectsVisitorWhenAnAccountAlreadyExists() {
        CompletableFuture<RegisterAccountResponseDTO> future = new CompletableFuture<>();
        service.registerVisitor(future, mock(RegisterVisitorRequestDTO.class), true);
        assertTrue(future.isCompletedExceptionally());
        verifyNoInteractions(userRepository);
    }

    @Test
    void failsVisitorRegistrationWhenParkRoleDoesNotExist() {
        RegisterVisitorRequestDTO request = mock(RegisterVisitorRequestDTO.class);
        when(request.defaults()).thenReturn(new RegisterAccountRequestDTO("visitor@weg.net", "password"));
        when(roleRepository.findByRole(RolesType.ROLE_PARK)).thenReturn(Optional.empty());
        CompletableFuture<RegisterAccountResponseDTO> future = new CompletableFuture<>();

        service.registerVisitor(future, request, false);
        assertTrue(future.isCompletedExceptionally());
        verifyNoInteractions(userRepository);
        verify(userMapper, never()).toEntity(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void failsGuardRegistrationWhenGuardRoleDoesNotExist() {
        CompletableFuture<ParkGuardRegisteredEvent> future = new CompletableFuture<>();
        RegisterGuardEvent event = new RegisterGuardEvent(
                future, "guard@weg.net", "password", "Guard", "1", "2", "location", "boss"
        );
        when(roleRepository.findByRole(RolesType.ROLE_GUARD)).thenReturn(Optional.empty());

        service.registerGuard(event, null);

        assertTrue(future.isCompletedExceptionally());
        verifyNoInteractions(userRepository);
        verify(userMapper, never()).toEntityFromGuardEvent(event);
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void failsRhRegistrationWhenRhRoleDoesNotExist() {
        CompletableFuture<DefaultRegisteredEvent> future = new CompletableFuture<>();
        RegisterRhEvent event = new RegisterRhEvent(future, "rh@weg.net", "password");
        when(roleRepository.findByRole(RolesType.ROLE_RH)).thenReturn(Optional.empty());

        service.registerRhAccount(event);

        assertTrue(future.isCompletedExceptionally());
        verifyNoInteractions(userRepository);
        verify(userMapper, never()).toEntityFromRhEvent(event);
    }

    @Test
    void reportsMissingAuthUserDuringCollaboratorRegistration() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.registerCollaborator(
                new CompletableFuture<>(), mock(RegisterCollaboratorRequestDTO.class), 99L
        ));
        verifyNoInteractions(roleRepository);
    }

    @Test
    void registersAdminAsActiveAndValidated() {
        User user = user("admin@gmail.com", "admin");
        when(roleRepository.findByRole(RolesType.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RolesType.ROLE_ADMIN)));
        when(userMapper.toRegisterResponse(any())).thenReturn(new RegisterAccountResponseDTO(UUID.randomUUID(), "admin@gmail.com"));

        var response = service.registerAdminAccount();

        assertNotNull(response);
        verify(userRepository).saveAndFlush(argThat(saved -> saved.getActive() && saved.getEmailValidated()));
    }

    @Test
    void startsCollaboratorAndVisitorValidationThroughEvents() {
        RegisterCollaboratorRequestDTO collaboratorRequest = mock(RegisterCollaboratorRequestDTO.class);
        RegisterVisitorRequestDTO visitorRequest = mock(RegisterVisitorRequestDTO.class);
        RegisterGuardEvent guardEvent = new RegisterGuardEvent(new CompletableFuture<>(), "g@weg.net", "password", "Guard", "1", "2", "loc", "boss");

        assertNotNull(service.checkBadgeNumberBeforeRegistering(collaboratorRequest));
        assertNotNull(service.checkVisitorAccountsBeforeRegistering(visitorRequest));
        assertNotNull(service.checkBadgeNumberBeforeRegisteringEvent(guardEvent));
        verify(publisher).publishEvent(any(ValidateCollaboratorEvent.class));
        verify(publisher).publishEvent(any(ValidateVisitorEvent.class));
        verify(publisher).publishEvent(any(ValidateCollaboratorByEvent.class));
    }

    @Test
    void reportsMissingAdminRole() {
        when(roleRepository.findByRole(RolesType.ROLE_ADMIN)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, service::registerAdminAccount);
    }

    private User user(String email, String password) {
        User user = new User(email, password);
        user.setUuid(UUID.randomUUID());
        return user;
    }
}

package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthUserServiceTest {
    private UserRepository userRepository;
    private ApplicationEventPublisher publisher;
    private AuthUserService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new AuthUserService(userRepository, publisher);
    }

    @Test
    void returnsPredefinedNameForAdmin() {
        assertEquals("Admin", service.getUserName(user(RolesType.ROLE_ADMIN)));
        verifyNoInteractions(publisher);
    }

    @Test
    void requestsNameFromTheCorrectModule() {
        doAnswer(invocation -> {
            Object event = invocation.getArgument(0);
            if (event instanceof GetRhUserNameEvent rhEvent) rhEvent.eventResponse().complete("RH User");
            if (event instanceof GetParkUserNameEvent parkEvent) parkEvent.eventResponse().complete("Park User");
            return null;
        }).when(publisher).publishEvent(any(Object.class));

        assertEquals("RH User", service.getUserName(user(RolesType.ROLE_RH)));
        assertEquals("Park User", service.getUserName(user(RolesType.ROLE_PARK)));
    }

    @Test
    void completesActiveLookupForExistingUser() {
        UUID uuid = UUID.randomUUID();
        User user = user(RolesType.ROLE_PARK);
        user.setActive(true);
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        var response = new java.util.concurrent.CompletableFuture<Boolean>();

        service.getActive(new IsParkUserActiveEvent(response, uuid));

        assertTrue(response.join());
    }

    @Test
    void completesActiveLookupExceptionallyWhenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        var response = new java.util.concurrent.CompletableFuture<Boolean>();

        service.getActive(new IsParkUserActiveEvent(response, uuid));

        assertTrue(response.isCompletedExceptionally());
        assertThrows(java.util.concurrent.CompletionException.class, response::join);
    }

    private User user(RolesType role) {
        User user = new User("user@weg.net", "password");
        user.setRole(new Role(role));
        user.setUuid(UUID.randomUUID());
        return user;
    }
}

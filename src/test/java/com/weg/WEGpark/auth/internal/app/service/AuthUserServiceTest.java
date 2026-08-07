package com.weg.WEGpark.auth.internal.app.service;

import com.weg.WEGpark.auth.GetAuthUserIdEvent;
import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.auth.internal.domain.model.Role;
import com.weg.WEGpark.auth.internal.domain.model.User;
import com.weg.WEGpark.auth.internal.infra.repository.UserRepository;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
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
    void resolvesUserIdFromAuthByUuid() {
        UUID userUuid = UUID.randomUUID();
        User user = user(RolesType.ROLE_RH);
        user.setId(42L);
        when(userRepository.findByUuid(userUuid)).thenReturn(Optional.of(user));
        var response = new java.util.concurrent.CompletableFuture<Long>();

        service.getUserId(new GetAuthUserIdEvent(response, userUuid));

        assertEquals(42L, response.join());

        UUID missingUuid = UUID.randomUUID();
        when(userRepository.findByUuid(missingUuid)).thenReturn(Optional.empty());
        var missingResponse = new java.util.concurrent.CompletableFuture<Long>();

        service.getUserId(new GetAuthUserIdEvent(missingResponse, missingUuid));

        assertTrue(missingResponse.isCompletedExceptionally());
        assertThrows(java.util.concurrent.CompletionException.class, missingResponse::join);
    }

    @Test
    void completesActiveLookupForExistingUser() {
        UUID uuid = UUID.randomUUID();
        User user = user(RolesType.ROLE_PARK);
        user.setActive(true);
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.of(user));
        var response = new java.util.concurrent.CompletableFuture<Boolean>();

        service.getActive(uuid, response);

        assertTrue(response.join());
    }

    @Test
    void completesActiveLookupExceptionallyWhenUserDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        when(userRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        var response = new java.util.concurrent.CompletableFuture<Boolean>();

        service.getActive(uuid, response);

        assertTrue(response.isCompletedExceptionally());
        assertThrows(java.util.concurrent.CompletionException.class, response::join);
    }

    @Test
    void resolvesSeveralActiveStatusesWithOneRepositoryCall() {
        User activeUser = user(RolesType.ROLE_PARK);
        activeUser.setId(1L);
        activeUser.setActive(true);
        User inactiveUser = user(RolesType.ROLE_RH);
        inactiveUser.setId(2L);
        inactiveUser.setActive(false);
        List<Long> userIds = List.of(1L, 2L);
        when(userRepository.findAllById(userIds)).thenReturn(List.of(activeUser, inactiveUser));
        var response = new java.util.concurrent.CompletableFuture<Map<Long, Boolean>>();

        service.getUsersActive(new GetUsersActiveEvent(response, userIds));

        assertEquals(Map.of(1L, true, 2L, false), response.join());
        verify(userRepository, times(1)).findAllById(userIds);
    }

    private User user(RolesType role) {
        User user = new User("user@weg.net", "password");
        user.setRole(new Role(role));
        user.setUuid(UUID.randomUUID());
        return user;
    }
}

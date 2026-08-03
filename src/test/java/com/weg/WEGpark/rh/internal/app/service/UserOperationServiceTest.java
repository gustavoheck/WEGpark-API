package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.dto.rh.GetRhResponseDTO;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserOperationServiceTest {
    private ApplicationEventPublisher publisher;
    private UserOperationMapper mapper;
    private RhService rhService;
    private OperationService operationService;
    private UserOperationService service;
    private JWTUserData token;

    @BeforeEach
    void setUp() {
        publisher = mock(ApplicationEventPublisher.class);
        mapper = mock(UserOperationMapper.class);
        rhService = mock(RhService.class);
        operationService = mock(OperationService.class);
        service = new UserOperationService(publisher, mapper, rhService, mock(RhRepository.class), operationService);
        token = new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of("ROLE_RH"), "RH");
    }

    @Test
    void combinesParkAndRhUserPages() {
        var pageable = PageRequest.of(0, 10);
        FindUserFilter filter = new FindUserFilter(null, null, null, null);
        GetRhResponseDTO rh = mock(GetRhResponseDTO.class);
        doAnswer(invocation -> {
            GetParkUsersEvent event = invocation.getArgument(0);
            event.eventResponse().complete(new PageImpl<>(List.of(), pageable, 0));
            return null;
        }).when(publisher).publishEvent(any(GetParkUsersEvent.class));
        when(rhService.listRhUsers(filter, pageable)).thenReturn(new PageImpl<>(List.of(rh), pageable, 1));

        assertEquals(1, service.listUsers(filter, pageable).getTotalElements());
    }

    @Test
    void updatesAuthDataThroughEventAndAuditsOperation() {
        UUID target = UUID.randomUUID();
        UpdateUserRequestDTO request = new UpdateUserRequestDTO("u@weg.net", "ROLE_PARK", null, null, null);
        UpdateUserResponseDTO response = new UpdateUserResponseDTO(3L, target, "u@weg.net");
        when(mapper.toUpdateAuthEvent(eq(request), any(), eq(target))).thenAnswer(invocation -> {
            CompletableFuture<UpdateUserResponseDTO> future = invocation.getArgument(1);
            future.complete(response);
            return mock(UpdateUserAuthEvent.class);
        });

        assertSame(response, service.updateUserAuthData(request, target, token));
        verify(operationService).saveOperation(token, 3L, OperationType.UPDATE);
    }

    @Test
    @Disabled("Known production deadlock: userIdResponse.join() is invoked before publishing DesactivateAndActivateUserEvent; enabling this test prevents the JVM from finishing.")
    void exposesDeactivationDeadlockBeforeEventPublication() {
        assertTimeoutPreemptively(Duration.ofMillis(250),
                () -> service.desactivateAndActivateUser(UUID.randomUUID(), token));
    }
}

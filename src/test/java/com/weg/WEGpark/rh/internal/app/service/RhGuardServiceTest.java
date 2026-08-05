package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.park.GuardUpdatedEvent;
import com.weg.WEGpark.park.ParkGuardRegisteredEvent;
import com.weg.WEGpark.rh.internal.app.mapper.RhGuardMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.dto.guard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RhGuardServiceTest {
    private ApplicationEventPublisher publisher;
    private RhGuardMapper mapper;
    private OperationService operationService;
    private RhGuardService service;
    private JWTUserData token;

    @BeforeEach
    void setUp() {
        publisher = mock(ApplicationEventPublisher.class);
        mapper = mock(RhGuardMapper.class);
        operationService = mock(OperationService.class);
        service = new RhGuardService(publisher, mapper, operationService);
        token = new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of("ROLE_RH"), "RH");
    }

    @Test
    void createsGuardThroughEventAndRecordsOperation() {
        RegisterGuardRequestDTO request = mock(RegisterGuardRequestDTO.class);
        ParkGuardRegisteredEvent event = new ParkGuardRegisteredEvent(3L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "2", "A", "Boss");
        RegisterGuardResponseDTO response = mock(RegisterGuardResponseDTO.class);
        when(mapper.toGuardRegisterEvent(eq(request), any())).thenAnswer(invocation -> {
            CompletableFuture<ParkGuardRegisteredEvent> future = invocation.getArgument(1);
            future.complete(event);
            return mock(com.weg.WEGpark.rh.RegisterGuardEvent.class);
        });
        when(mapper.toGuardRegisterResponse(event)).thenReturn(response);

        assertSame(response, service.createGuard(request, token));
        verify(operationService).saveOperation(token, 3L, OperationType.CREATE);
    }

    @Test
    void updatesGuardThroughEventAndRecordsOperation() {
        UpdateGuardRequestDTO request = mock(UpdateGuardRequestDTO.class);
        UUID guardUuid = UUID.randomUUID();
        GuardUpdatedEvent event = new GuardUpdatedEvent(3L, guardUuid, "Guard", "1", "2", "A", "Boss");
        UpdateGuardResponseDTO response = mock(UpdateGuardResponseDTO.class);
        when(mapper.toGuardUpdateEvent(eq(request), eq(guardUuid), any())).thenAnswer(invocation -> {
            CompletableFuture<GuardUpdatedEvent> future = invocation.getArgument(2);
            future.complete(event);
            return mock(com.weg.WEGpark.rh.UpdateGuardEvent.class);
        });
        when(mapper.toGuardUpdateResponse(event)).thenReturn(response);

        assertSame(response, service.updateRegistrationData(request, guardUuid, token));
        verify(operationService).saveOperation(token, 3L, OperationType.UPDATE);
    }
}

package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.GuardRegisteredEvent;
import com.weg.WEGpark.park.GuardUpdatedEvent;
import com.weg.WEGpark.park.ParkGuardRegisteredEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Guard;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.rh.UpdateGuardEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuardServiceTest {
    private ParkUserRepository repository;
    private GuardMapper mapper;
    private GuardService service;
    private Guard guard;

    @BeforeEach
    void setUp() {
        repository = mock(ParkUserRepository.class);
        mapper = mock(GuardMapper.class);
        service = new GuardService(mapper, repository);
        guard = new Guard(3L, UUID.randomUUID(), "g@weg.net", "1", "Guard", "12", "A", "Boss");
    }

    @Test
    void registersGuardAndCompletesResponse() {
        GuardRegisteredEvent event = mock(GuardRegisteredEvent.class);
        CompletableFuture<ParkGuardRegisteredEvent> future = new CompletableFuture<>();
        ParkGuardRegisteredEvent response = mock(ParkGuardRegisteredEvent.class);
        when(event.registerResponse()).thenReturn(future);
        when(mapper.toEntity(event)).thenReturn(guard);
        when(mapper.toEventResponse(event)).thenReturn(response);

        service.registerGuard(event);

        assertEquals(ParkUserType.GUARD, guard.getUserType());
        assertSame(response, future.join());
        verify(repository).save(guard);
    }

    @Test
    void updatesExistingGuardAndRejectsUnknownOne() {
        UpdateGuardEvent event = mock(UpdateGuardEvent.class);
        CompletableFuture<GuardUpdatedEvent> future = new CompletableFuture<>();
        GuardUpdatedEvent response = mock(GuardUpdatedEvent.class);
        when(event.uuid()).thenReturn(guard.getUuid());
        when(event.eventResponse()).thenReturn(future);
        when(repository.findByUuid(guard.getUuid())).thenReturn(Optional.of(guard));
        when(mapper.toUpdatedEvent(guard)).thenReturn(response);

        service.updateGuard(event);
        assertSame(response, future.join());
        verify(repository).save(guard);

        when(repository.findByUuid(guard.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateGuard(event));
    }
}

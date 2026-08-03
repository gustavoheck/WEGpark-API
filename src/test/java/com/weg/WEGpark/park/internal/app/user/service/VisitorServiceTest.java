package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.VisitorRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorResponseDTO;
import com.weg.WEGpark.rh.UpdateVisitorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VisitorServiceTest {
    private VisitorRepository visitorRepository;
    private ParkUserRepository parkUserRepository;
    private VisitorMapper mapper;
    private VisitorService service;
    private Visitor visitor;

    @BeforeEach
    void setUp() {
        visitorRepository = mock(VisitorRepository.class);
        parkUserRepository = mock(ParkUserRepository.class);
        mapper = mock(VisitorMapper.class);
        service = new VisitorService(mapper, visitorRepository, parkUserRepository);
        visitor = new Visitor(2L, UUID.randomUUID(), "v@weg.net", "1", "Visitor", "Company", "123");
    }

    @Test
    void registersVisitorAndCompletesRegistrationResponse() {
        VisitorRegisteredEvent event = mock(VisitorRegisteredEvent.class);
        CompletableFuture<com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO> future = new CompletableFuture<>();
        when(mapper.toEntity(event)).thenReturn(visitor);
        when(event.futureResponse()).thenReturn(future);
        when(event.email()).thenReturn(visitor.getEmail());

        service.registerVisitor(event);

        assertEquals(ParkUserType.VISITOR, visitor.getUserType());
        assertEquals(visitor.getUuid(), future.join().uuid());
        verify(parkUserRepository).save(visitor);
    }

    @Test
    void updatesLoggedVisitorAndRejectsRh() {
        UpdateVisitorRequestDTO request = mock(UpdateVisitorRequestDTO.class);
        UpdateVisitorResponseDTO response = mock(UpdateVisitorResponseDTO.class);
        JWTUserData visitorToken = new JWTUserData(visitor.getUuid(), visitor.getEmail(), List.of(RolesType.ROLE_PARK.name()), visitor.getName());
        when(visitorRepository.findByUuid(visitor.getUuid())).thenReturn(Optional.of(visitor));
        when(mapper.toUpdateResponse(visitor)).thenReturn(response);

        assertSame(response, service.updateVisitorRequest(request, visitorToken));
        verify(visitorRepository).save(visitor);

        JWTUserData rhToken = new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH");
        assertThrows(AccessDeniedException.class, () -> service.updateVisitorRequest(request, rhToken));
    }

    @Test
    void updatesFromEventAndCompletesMissingVisitorExceptionally() {
        UpdateVisitorEvent event = mock(UpdateVisitorEvent.class);
        CompletableFuture<UpdateVisitorResponseDTO> response = new CompletableFuture<>();
        UpdateVisitorResponseDTO mapped = mock(UpdateVisitorResponseDTO.class);
        when(event.parkUserUuid()).thenReturn(visitor.getUuid());
        when(event.eventResponse()).thenReturn(response);
        when(visitorRepository.findByUuid(visitor.getUuid())).thenReturn(Optional.of(visitor));
        when(mapper.toUpdateResponseFromEvent(event)).thenReturn(mapped);

        service.updateVisitorEvent(event);
        assertSame(mapped, response.join());

        CompletableFuture<UpdateVisitorResponseDTO> missing = new CompletableFuture<>();
        when(event.eventResponse()).thenReturn(missing);
        when(visitorRepository.findByUuid(visitor.getUuid())).thenReturn(Optional.empty());
        service.updateVisitorEvent(event);
        assertTrue(missing.isCompletedExceptionally());
    }
}

package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.DefaultRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.GetRhUserIdEvent;
import com.weg.WEGpark.rh.GetRhUserNameEvent;
import com.weg.WEGpark.rh.internal.app.mapper.RhMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.domain.model.Rh;
import com.weg.WEGpark.rh.internal.dto.rh.*;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RhServiceTest {
    private RhMapper mapper;
    private RhRepository repository;
    private OperationService operationService;
    private ApplicationEventPublisher publisher;
    private RhService service;
    private Rh rh;
    private JWTUserData token;

    @BeforeEach
    void setUp() {
        mapper = mock(RhMapper.class);
        repository = mock(RhRepository.class);
        operationService = mock(OperationService.class);
        publisher = mock(ApplicationEventPublisher.class);
        service = new RhService(mapper, repository, operationService, publisher);
        rh = new Rh(1L, UUID.randomUUID(), "rh@weg.net", "1", "RH", "2");
        token = new JWTUserData(rh.getUuid(), rh.getEmail(), List.of(RolesType.ROLE_RH.name()), rh.getName());
    }

    @Test
    void registersRhThroughAuthEventAndRecordsOperation() {
        RegisterRhRequestDTO request = new RegisterRhRequestDTO(new com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO("new@weg.net", "password"), "1", "New RH", "2");
        DefaultRegisteredEvent event = new DefaultRegisteredEvent(UUID.randomUUID(), 3L, "new@weg.net");
        RegisterRhResponseDTO response = mock(RegisterRhResponseDTO.class);
        when(mapper.toRegisterEvent(eq(request.defaults()), any())).thenAnswer(invocation -> {
            CompletableFuture<DefaultRegisteredEvent> future = invocation.getArgument(1);
            future.complete(event);
            return mock(com.weg.WEGpark.rh.RegisterRhEvent.class);
        });
        when(repository.existsByEmail(request.defaults().email())).thenReturn(false);
        when(mapper.toEntity(request)).thenReturn(rh);
        when(mapper.toRegisterResponse(rh)).thenReturn(response);

        assertSame(response, service.registerRh(request, token));
        assertAll(() -> assertEquals(event.id(), rh.getId()), () -> assertEquals(event.uuid(), rh.getUuid()), () -> assertEquals(event.email(), rh.getEmail()));
        verify(operationService).saveOperation(token, event.id(), OperationType.CREATE);
    }

    @Test
    void updatesFindsAndGetsNameForRhProfile() {
        UpdateRhRequestDTO update = new UpdateRhRequestDTO("2", "Updated", "3");
        UpdateRhResponseDTO updated = mock(UpdateRhResponseDTO.class);
        GetRhResponseDTO profile = mock(GetRhResponseDTO.class);
        when(repository.findByUuid(rh.getUuid())).thenReturn(Optional.of(rh));
        when(mapper.toUpdateResponse(rh)).thenReturn(updated);
        when(mapper.toGetResponse(rh)).thenReturn(profile);

        assertSame(updated, service.updateRh(update, rh.getUuid(), token));
        assertSame(updated, service.updateMyProfile(update, token));
        assertSame(profile, service.findMyProfile(token));
        assertSame(profile, service.findUserByUuid(rh.getUuid()));
        CompletableFuture<String> name = new CompletableFuture<>();
        service.getUserName(new GetRhUserNameEvent(name, rh.getUuid()));
        assertEquals(rh.getName(), name.join());
        CompletableFuture<Long> id = new CompletableFuture<>();
        service.getUserId(new GetRhUserIdEvent(id, rh.getUuid()));
        assertEquals(rh.getId(), id.join());
    }

    @Test
    void enforcesRhRoleAndReturnsPagedRhUsersWithoutFilter() {
        JWTUserData parkToken = new JWTUserData(UUID.randomUUID(), "p@weg.net", List.of(RolesType.ROLE_PARK.name()), "P");
        assertThrows(AccessDeniedException.class, () -> service.findMyProfile(parkToken));
        var pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(rh), pageable, 1));
        when(mapper.toGetResponse(rh)).thenReturn(mock(GetRhResponseDTO.class));
        assertEquals(1, service.listRhUsers(new FindUserFilter(null, null, null, null), pageable).getTotalElements());
    }

    @Test
    void rejectsMissingRhProfile() {
        when(repository.findByUuid(rh.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findMyProfile(token));
    }
}

package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.rh.FindParkUserEvent;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.dto.rh.GetRhResponseDTO;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
        service = new UserOperationService(publisher, mapper, rhService, operationService);
        token = new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of("ROLE_RH"), "RH");
    }

    @Test
    void combinesParkAndRhUserPages() {
        var pageable = PageRequest.of(0, 10);
        var sourcePageable = PageRequest.of(0, 10, Sort.by("id"));
        FindUserFilter filter = new FindUserFilter(null, null, null, null);
        GetRhResponseDTO rh = mock(GetRhResponseDTO.class);
        UserSearchResult rhResult = new UserSearchResult(
                1L, UUID.randomUUID(), null, null, "RH", null, null, true, rh);
        doAnswer(invocation -> {
            GetParkUsersEvent event = invocation.getArgument(0);
            event.eventResponse().complete(new PageImpl<>(List.of(), sourcePageable, 0));
            return null;
        }).when(publisher).publishEvent(any(GetParkUsersEvent.class));
        when(rhService.listRhUsers(filter, sourcePageable))
                .thenReturn(new PageImpl<>(List.of(rhResult), sourcePageable, 1));

        var response = service.listUsers(filter, pageable);
        assertEquals(1, response.getTotalElements());
        assertSame(rh, response.getContent().getFirst());
    }

    @Test
    void findsRhUserLocallyAndParkUserThroughEvent() {
        UUID rhUuid = UUID.randomUUID();
        GetRhResponseDTO rhResponse = mock(GetRhResponseDTO.class);
        when(rhService.findUserByUuid(rhUuid)).thenReturn(rhResponse);

        assertSame(rhResponse, service.findUser(rhUuid, RolesType.ROLE_RH));

        UUID parkUuid = UUID.randomUUID();
        Record parkResponse = new TestUserResponse(parkUuid);
        doAnswer(invocation -> {
            FindParkUserEvent event = invocation.getArgument(0);
            assertEquals(parkUuid, event.userUuid());
            event.eventResponse().complete(parkResponse);
            return null;
        }).when(publisher).publishEvent(any(FindParkUserEvent.class));

        assertSame(parkResponse, service.findUser(parkUuid, RolesType.ROLE_PARK));
        verify(publisher).publishEvent(any(FindParkUserEvent.class));
        assertThrows(NotFoundException.class,
                () -> service.findUser(UUID.randomUUID(), RolesType.ROLE_ADMIN));
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
    void deactivatesUserThroughEventAndAuditsOperation() {
        UUID target = UUID.randomUUID();
        doAnswer(invocation -> {
            com.weg.WEGpark.rh.DesactivateAndActivateUserEvent event = invocation.getArgument(0);
            assertEquals(target, event.uuid());
            event.userIdResponse().complete(7L);
            return null;
        }).when(publisher).publishEvent(any(com.weg.WEGpark.rh.DesactivateAndActivateUserEvent.class));

        service.desactivateAndActivateUser(target, token);

        verify(operationService).saveOperation(token, 7L, OperationType.DESACTIVATE);
    }

    private record TestUserResponse(UUID uuid) {
    }
}

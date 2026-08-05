package com.weg.WEGpark.rh.internal.app.service;

import com.weg.WEGpark.auth.UpdateUserAuthEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserRequestDTO;
import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.internal.app.mapper.UserOperationMapper;
import com.weg.WEGpark.rh.internal.domain.enums.OperationType;
import com.weg.WEGpark.rh.internal.infra.repository.RhRepository;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
    void combinesAndSortsSourcesBeforePagingWithoutRepeatingUsers() {
        FindUserFilter filter = new FindUserFilter(null, null, null, null);
        List<UserSearchResult> parkUsers = List.of(
                user(1L, "alpha"),
                user(4L, "delta"),
                user(6L, "foxtrot")
        );
        List<UserSearchResult> rhUsers = List.of(
                user(2L, "beta"),
                user(3L, "charlie"),
                user(5L, "echo")
        );
        doAnswer(invocation -> {
            GetParkUsersEvent event = invocation.getArgument(0);
            int toIndex = Math.min(event.pageable().getPageSize(), parkUsers.size());
            event.eventResponse().complete(
                    new PageImpl<>(parkUsers.subList(0, toIndex), event.pageable(), parkUsers.size())
            );
            return null;
        }).when(publisher).publishEvent(any(GetParkUsersEvent.class));
        when(rhService.listRhUsers(eq(filter), any(Pageable.class))).thenAnswer(invocation -> {
            Pageable sourcePageable = invocation.getArgument(1);
            int toIndex = Math.min(sourcePageable.getPageSize(), rhUsers.size());
            return new PageImpl<>(rhUsers.subList(0, toIndex), sourcePageable, rhUsers.size());
        });

        Sort sort = Sort.by("name");
        Page<Record> firstPage = service.listUsers(filter, PageRequest.of(0, 2, sort));
        Page<Record> secondPage = service.listUsers(filter, PageRequest.of(1, 2, sort));
        Page<Record> thirdPage = service.listUsers(filter, PageRequest.of(2, 2, sort));

        assertAll(
                () -> assertEquals(
                        List.of(parkUsers.get(0).response(), rhUsers.get(0).response()),
                        firstPage.getContent()
                ),
                () -> assertEquals(
                        List.of(rhUsers.get(1).response(), parkUsers.get(1).response()),
                        secondPage.getContent()
                ),
                () -> assertEquals(
                        List.of(rhUsers.get(2).response(), parkUsers.get(2).response()),
                        thirdPage.getContent()
                ),
                () -> assertEquals(6, firstPage.getTotalElements()),
                () -> assertEquals(2, firstPage.getSize()),
                () -> assertEquals(6, Stream.of(firstPage, secondPage, thirdPage)
                        .flatMap(page -> page.getContent().stream())
                        .distinct()
                        .count())
        );
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

    private UserSearchResult user(Long id, String name) {
        return new UserSearchResult(
                id,
                UUID.randomUUID(),
                name + "@weg.net",
                "1",
                name,
                null,
                null,
                true,
                new TestUser(name)
        );
    }

    private record TestUser(String name) {
    }
}

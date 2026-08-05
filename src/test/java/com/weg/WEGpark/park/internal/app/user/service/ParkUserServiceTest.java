package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.GetParkUserIdEvent;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.dto.user.collaborator.GetCollaboratorResponseDTO;
import com.weg.WEGpark.park.internal.dto.user.visitor.GetVisitorResponseDTO;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.FindParkUserEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.IsParkUserActiveEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ParkUserServiceTest {
    private ParkUserRepository repository;
    private CollaboratorRepository collaboratorRepository;
    private VisitorRepository visitorRepository;
    private ApplicationEventPublisher publisher;
    private CollaboratorMapper collaboratorMapper;
    private VisitorMapper visitorMapper;
    private ParkUserService service;
    private Collaborator collaborator;

    @BeforeEach
    void setUp() {
        repository = mock(ParkUserRepository.class);
        collaboratorRepository = mock(CollaboratorRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        collaboratorMapper = mock(CollaboratorMapper.class);
        visitorMapper = mock(VisitorMapper.class);
        service = new ParkUserService(repository, collaboratorRepository, visitorRepository, visitorMapper, collaboratorMapper, mock(GuardMapper.class), publisher);
        collaborator = new Collaborator(1L, UUID.randomUUID(), "c@weg.net", "1", "Collaborator", "2", "A");
        collaborator.setUserType(ParkUserType.COLLABORATOR);
    }

    @Test
    void verifiesEmailAvailabilityAndReturnsParkUserData() {
        when(repository.existsByEmail("c@weg.net")).thenReturn(true);
        assertTrue(service.verifyParkUserToRegister("c@weg.net"));
        when(repository.findByUuid(collaborator.getUuid())).thenReturn(Optional.of(collaborator));
        CompletableFuture<String> future = new CompletableFuture<>();
        service.getUserName(new GetParkUserNameEvent(future, collaborator.getUuid()));
        assertEquals("Collaborator", future.join());
        CompletableFuture<Long> idFuture = new CompletableFuture<>();
        service.getUserId(new GetParkUserIdEvent(idFuture, collaborator.getUuid()));
        assertEquals(collaborator.getId(), idFuture.join());
    }

    @Test
    void returnsParkUserByUuidThroughEvent() {
        GetCollaboratorResponseDTO response = mock(GetCollaboratorResponseDTO.class);
        when(repository.findByUuid(collaborator.getUuid())).thenReturn(Optional.of(collaborator));
        when(collaboratorMapper.toResponse(collaborator, true)).thenReturn(response);
        doAnswer(invocation -> {
            IsParkUserActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(true);
            return null;
        }).when(publisher).publishEvent(any(IsParkUserActiveEvent.class));
        CompletableFuture<Record> eventResponse = new CompletableFuture<>();

        service.findParkUser(new FindParkUserEvent(eventResponse, collaborator.getUuid()));

        assertSame(response, eventResponse.join());
    }

    @Test
    void rejectsRhProfileAndMissingParkUser() {
        assertThrows(AccessDeniedException.class, () -> service.findMyProfile(new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH")));
        JWTUserData park = new JWTUserData(collaborator.getUuid(), collaborator.getEmail(), List.of(RolesType.ROLE_PARK.name()), collaborator.getName());
        when(repository.findByUuid(collaborator.getUuid())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.findMyProfile(park));
    }

    @Test
    void listsAllParkUsersWhenNoFilterIsProvided() {
        var pageable = PageRequest.of(0, 10);
        FindUserFilter filter = new FindUserFilter(null, null, null, null);
        GetCollaboratorResponseDTO mappedResponse = mock(GetCollaboratorResponseDTO.class);
        doAnswer(invocation -> {
            GetUsersActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(Map.of(collaborator.getId(), true));
            return null;
        }).when(publisher).publishEvent(any(GetUsersActiveEvent.class));
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(collaborator), pageable, 1));
        when(collaboratorMapper.toResponse(collaborator, true)).thenReturn(mappedResponse);
        CompletableFuture<Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        assertEquals(1, response.join().getTotalElements());
        assertSame(mappedResponse, response.join().getContent().getFirst().response());
    }

    @Test
    void returnsVisitorWhenFilteringByCpfWithoutRunningOtherQueries() {
        var pageable = PageRequest.of(0, 10);
        Visitor visitor = new Visitor(2L, UUID.randomUUID(), "v@weg.net", "2", "Visitor", "WEG", "12345678900");
        visitor.setUserType(ParkUserType.VISITOR);
        FindUserFilter filter = new FindUserFilter(null, null, visitor.getCpf(), null);
        GetVisitorResponseDTO mappedResponse = mock(GetVisitorResponseDTO.class);
        doAnswer(invocation -> {
            GetUsersActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(Map.of(visitor.getId(), true));
            return null;
        }).when(publisher).publishEvent(any(GetUsersActiveEvent.class));
        when(visitorRepository.findVisitorByCpf(visitor.getCpf(), pageable))
                .thenReturn(new PageImpl<>(List.of(visitor), pageable, 1));
        when(visitorMapper.toResponse(visitor, true)).thenReturn(mappedResponse);
        CompletableFuture<Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        Page<UserSearchResult> result = response.join();
        assertEquals(1, result.getTotalElements());
        assertSame(mappedResponse, result.getContent().getFirst().response());
        verify(visitorRepository).findVisitorByCpf(visitor.getCpf(), pageable);
        verify(repository, never()).findAll(pageable);
    }

    @Test
    void rejectsMoreThanOneFilterWithoutQueryingRepositories() {
        var pageable = PageRequest.of(0, 10);
        FindUserFilter filter = new FindUserFilter("User", "123", null, null);
        CompletableFuture<Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        CompletionException exception = assertThrows(
                CompletionException.class, response::join);
        assertInstanceOf(com.weg.WEGpark.shared.exception.MoreThenOneFilterException.class, exception.getCause());
        verifyNoInteractions(repository, collaboratorRepository, visitorRepository);
    }

    @Test
    void filtersActiveUsersBeforeApplyingPagination() {
        var pageable = PageRequest.of(1, 1);
        Collaborator inactive = new Collaborator(2L, UUID.randomUUID(), "i@weg.net", "2", "Inactive", "2", "A");
        inactive.setUserType(ParkUserType.COLLABORATOR);
        Collaborator active = new Collaborator(3L, UUID.randomUUID(), "a@weg.net", "3", "Active", "3", "A");
        active.setUserType(ParkUserType.COLLABORATOR);
        FindUserFilter filter = new FindUserFilter(null, null, null, true);
        GetCollaboratorResponseDTO mappedResponse = mock(GetCollaboratorResponseDTO.class);
        doAnswer(invocation -> {
            GetUsersActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(Map.of(collaborator.getId(), true, inactive.getId(), false, active.getId(), true));
            return null;
        }).when(publisher).publishEvent(any(GetUsersActiveEvent.class));
        when(repository.findAll(pageable.getSort()))
                .thenReturn(List.of(collaborator, inactive, active));
        when(collaboratorMapper.toResponse(active, true)).thenReturn(mappedResponse);
        CompletableFuture<Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        Page<UserSearchResult> result = response.join();
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getNumberOfElements());
        assertEquals(active.getUuid(), result.getContent().getFirst().uuid());
        verify(publisher).publishEvent(any(GetUsersActiveEvent.class));
    }

}

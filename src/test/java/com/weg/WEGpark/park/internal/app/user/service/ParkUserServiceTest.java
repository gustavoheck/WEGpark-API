package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.GetUsersActiveEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.GetParkUserIdEvent;
import com.weg.WEGpark.park.GetParkUserNameEvent;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.GuardMapper;
import com.weg.WEGpark.park.internal.app.user.mapper.VisitorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.domain.model.users.Visitor;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.internal.infra.repository.VisitorRepository;
import com.weg.WEGpark.rh.GetParkUsersEvent;
import com.weg.WEGpark.rh.UserSearchResult;
import com.weg.WEGpark.rh.shared.filter.FindUserFilter;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private GuardMapper guardMapper;
    private ParkUserService service;
    private Collaborator collaborator;

    @BeforeEach
    void setUp() {
        repository = mock(ParkUserRepository.class);
        collaboratorRepository = mock(CollaboratorRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        publisher = mock(ApplicationEventPublisher.class);
        visitorMapper = mock(VisitorMapper.class);
        guardMapper = mock(GuardMapper.class);
        collaboratorMapper = mock(CollaboratorMapper.class);
        service = new ParkUserService(repository, collaboratorRepository, visitorRepository, visitorMapper,
                collaboratorMapper, guardMapper, publisher);
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
        doAnswer(invocation -> {
            GetUsersActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(Map.of(collaborator.getId(), true));
            return null;
        }).when(publisher).publishEvent(any(GetUsersActiveEvent.class));
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(collaborator), pageable, 1));
        when(collaboratorMapper.toResponse(collaborator, true)).thenReturn(mock(com.weg.WEGpark.park.internal.dto.user.collaborator.GetCollaboratorResponseDTO.class));
        CompletableFuture<org.springframework.data.domain.Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        assertEquals(1, response.join().getTotalElements());
    }

    @Test
    void returnsTheMappedVisitorPageWhenFilteringByCpf() {
        var pageable = PageRequest.of(0, 10);
        FindUserFilter filter = new FindUserFilter(null, null, "12345678900", null);
        Visitor visitor = new Visitor(2L, UUID.randomUUID(), "v@weg.net", "2", "Visitor", "Company", "12345678900");
        visitor.setUserType(ParkUserType.VISITOR);
        var visitorResponse = mock(com.weg.WEGpark.park.internal.dto.user.visitor.GetVisitorResponseDTO.class);
        when(visitorRepository.findVisitorByCpf(filter.cpf(), pageable))
                .thenReturn(new PageImpl<>(List.of(visitor), pageable, 1));
        when(visitorMapper.toResponse(visitor, true)).thenReturn(visitorResponse);
        doAnswer(invocation -> {
            GetUsersActiveEvent event = invocation.getArgument(0);
            event.eventResponse().complete(Map.of(visitor.getId(), true));
            return null;
        }).when(publisher).publishEvent(any(GetUsersActiveEvent.class));
        CompletableFuture<org.springframework.data.domain.Page<UserSearchResult>> response = new CompletableFuture<>();

        service.findParkUsers(new GetParkUsersEvent(response, filter, pageable), pageable);

        assertEquals(visitorResponse, response.join().getContent().getFirst().response());
        assertEquals(1, response.join().getTotalElements());
        verify(repository, never()).findAll(pageable);
        verifyNoInteractions(collaboratorRepository);
    }
}

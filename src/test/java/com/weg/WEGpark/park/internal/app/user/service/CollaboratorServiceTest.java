package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.shared.dto.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorRequestDTO;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CollaboratorServiceTest {
    private CollaboratorRepository collaboratorRepository;
    private ParkUserRepository parkUserRepository;
    private CollaboratorMapper mapper;
    private CollaboratorService service;
    private Collaborator collaborator;

    @BeforeEach
    void setUp() {
        collaboratorRepository = mock(CollaboratorRepository.class);
        parkUserRepository = mock(ParkUserRepository.class);
        mapper = mock(CollaboratorMapper.class);
        service = new CollaboratorService(collaboratorRepository, parkUserRepository, mapper);
        collaborator = new Collaborator(1L, UUID.randomUUID(), "c@weg.net", "1", "Collaborator", "123", "A");
        collaborator.setUserType(ParkUserType.COLLABORATOR);
    }

    @Test
    void returnsExistingCollaboratorIdOrNullDuringValidation() {
        when(collaboratorRepository.findByBadgeNumberOrEmail("123", "c@weg.net")).thenReturn(Optional.of(collaborator));
        assertEquals(1L, service.verifyCollaboratorToRegister("123", "c@weg.net"));
        when(collaboratorRepository.findByBadgeNumberOrEmail("none", "n@weg.net")).thenReturn(Optional.empty());
        assertNull(service.verifyCollaboratorToRegister("none", "n@weg.net"));
    }

    @Test
    void registersCollaboratorAndCompletesFuture() {
        CompletableFuture<com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO> response = new CompletableFuture<>();
        CollaboratorRegisteredEvent event = mock(CollaboratorRegisteredEvent.class);
        when(event.futureResponse()).thenReturn(response);
        when(mapper.toEntity(event)).thenReturn(collaborator);

        service.registerCollaborator(event);

        assertEquals(ParkUserType.COLLABORATOR, collaborator.getUserType());
        assertEquals(collaborator.getUuid(), response.join().uuid());
        verify(parkUserRepository).save(collaborator);
    }

    @Test
    void updatesOnlyLoggedCollaborator() {
        UpdateCollaboratorRequestDTO request = mock(UpdateCollaboratorRequestDTO.class);
        UpdateCollaboratorResponseDTO response = mock(UpdateCollaboratorResponseDTO.class);
        JWTUserData token = new JWTUserData(collaborator.getUuid(), collaborator.getEmail(), List.of(RolesType.ROLE_PARK.name()), collaborator.getName());
        when(collaboratorRepository.findByUuid(collaborator.getUuid())).thenReturn(Optional.of(collaborator));
        when(mapper.toUpdateResponse(collaborator)).thenReturn(response);

        assertSame(response, service.updateCollaboratorRequest(request, token));
        verify(mapper).updateFromDTO(request, collaborator);
        verify(collaboratorRepository).save(collaborator);
    }

    @Test
    void rejectsRhAndWrongParkUserTypeForSelfUpdate() {
        UpdateCollaboratorRequestDTO request = mock(UpdateCollaboratorRequestDTO.class);
        JWTUserData rh = new JWTUserData(UUID.randomUUID(), "rh@weg.net", List.of(RolesType.ROLE_RH.name()), "RH");
        assertThrows(AccessDeniedException.class, () -> service.updateCollaboratorRequest(request, rh));

        collaborator.setUserType(ParkUserType.GUARD);
        JWTUserData token = new JWTUserData(collaborator.getUuid(), collaborator.getEmail(), List.of(RolesType.ROLE_PARK.name()), collaborator.getName());
        when(collaboratorRepository.findByUuid(collaborator.getUuid())).thenReturn(Optional.of(collaborator));
        assertThrows(AccessDeniedException.class, () -> service.updateCollaboratorRequest(request, token));
    }

    @Test
    void updatesFromRhEventAndSignalsMissingCollaborator() {
        UUID uuid = collaborator.getUuid();
        CompletableFuture<UpdateCollaboratorResponseDTO> response = new CompletableFuture<>();
        UpdateCollaboratorEvent event = mock(UpdateCollaboratorEvent.class);
        when(event.parkUserUuid()).thenReturn(uuid);
        when(event.eventResponse()).thenReturn(response);
        UpdateCollaboratorResponseDTO mapped = mock(UpdateCollaboratorResponseDTO.class);
        when(mapper.toUpdateResponseFromEvent(event)).thenReturn(mapped);
        when(collaboratorRepository.findByUuid(uuid)).thenReturn(Optional.of(collaborator));

        service.updateCollaboratorEvent(event);
        assertSame(mapped, response.join());
        verify(collaboratorRepository).save(collaborator);

        CompletableFuture<UpdateCollaboratorResponseDTO> missing = new CompletableFuture<>();
        when(event.eventResponse()).thenReturn(missing);
        when(collaboratorRepository.findByUuid(uuid)).thenReturn(Optional.empty());
        service.updateCollaboratorEvent(event);
        assertTrue(missing.isCompletedExceptionally());
    }
}

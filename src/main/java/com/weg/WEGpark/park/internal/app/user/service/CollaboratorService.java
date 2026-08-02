package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.internal.infra.security.config.JWTUserData;
import com.weg.WEGpark.auth.shared.enums.RolesType;
import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorRequestDTO;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;
import com.weg.WEGpark.rh.UpdateCollaboratorEvent;
import com.weg.WEGpark.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;
    private final ParkUserRepository parkUserRepository;
    private final CollaboratorMapper collaboratorMapper;

    public Long verifyCollaboratorToRegister (String badgeNumber, String email) {
        Optional<Collaborator> collaborator = collaboratorRepository.findByBadgeNumberOrEmail(badgeNumber, email);

        return collaborator.map(ParkUser::getId).orElse(null);
    }

    public void registerCollaborator (CollaboratorRegisteredEvent event) {
        Collaborator collaborator = collaboratorMapper.toEntity(event);
        collaborator.setUserType(ParkUserType.COLLABORATOR);

        parkUserRepository.save(collaborator);

        event.futureResponse().complete(new RegisterAccountResponseDTO(collaborator.getUuid(), collaborator.getEmail()));
    }

    @Transactional
    public UpdateCollaboratorResponseDTO updateCollaboratorRequest (UpdateCollaboratorRequestDTO request, JWTUserData jwtUserData) {
        if (jwtUserData.roles().contains(RolesType.ROLE_RH.name())) {
            throw new AccessDeniedException("Rh users can not update park user registration data");
        }

        Collaborator collaborator = collaboratorRepository.findByUuid(jwtUserData.uuid())
                .orElseThrow(() -> new NotFoundException("Any collaborator was found by %s uuid".formatted(jwtUserData.uuid())));

        if (collaborator.getUserType() != ParkUserType.COLLABORATOR) {
            throw new AccessDeniedException("Only collaborators can update collaborator registration data");
        }

        collaboratorMapper.updateFromDTO(request, collaborator);

        collaboratorRepository.save(collaborator);

        return collaboratorMapper.toUpdateResponse(collaborator);
    }

    public void updateCollaboratorEvent (UpdateCollaboratorEvent event) {
        Optional<Collaborator> optCollaborator = collaboratorRepository.findByUuid(event.parkUserUuid());

        if (optCollaborator.isPresent()) {
            Collaborator collaborator = optCollaborator.get();

            collaboratorMapper.updateFromEvent(event, collaborator);

            collaboratorRepository.save(collaborator);

            event.eventResponse().complete(collaboratorMapper.toUpdateResponseFromEvent(event));
        }
        event.eventResponse().completeExceptionally(new NotFoundException("Any collaborator was found by %s uuid".formatted(event.parkUserUuid())));
    }
}

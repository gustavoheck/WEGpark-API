package com.weg.WEGpark.park.internal.app.user.service;

import com.weg.WEGpark.auth.CollaboratorRegisteredEvent;
import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.park.internal.app.user.mapper.CollaboratorMapper;
import com.weg.WEGpark.park.internal.domain.enums.user.ParkUserType;
import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import com.weg.WEGpark.park.internal.infra.repository.ParkUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}

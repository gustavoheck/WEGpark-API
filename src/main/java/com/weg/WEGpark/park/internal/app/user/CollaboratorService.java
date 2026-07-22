package com.weg.WEGpark.park.internal.app.user;

import com.weg.WEGpark.park.internal.domain.model.users.Collaborator;
import com.weg.WEGpark.park.internal.domain.model.users.ParkUser;
import com.weg.WEGpark.park.internal.infra.repository.CollaboratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollaboratorService {

    private final CollaboratorRepository collaboratorRepository;

    public Long verifyCollaboratorToRegisterByBadgeNumber (String badgeNumber) {
        Optional<Collaborator> collaborator = collaboratorRepository.findByBadgeNumber(badgeNumber);

        return collaborator.map(ParkUser::getId).orElse(null);
    }
}

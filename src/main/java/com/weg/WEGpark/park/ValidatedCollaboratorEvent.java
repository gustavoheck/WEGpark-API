package com.weg.WEGpark.park;

import com.weg.WEGpark.auth.shared.dto.collaborator.RegisterCollaboratorRequestDTO;

public record ValidatedCollaboratorEvent(

        RegisterCollaboratorRequestDTO request,
        Long collaboratorId

) {
}

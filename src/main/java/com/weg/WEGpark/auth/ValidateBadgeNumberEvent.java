package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.shared.dto.collaborator.RegisterCollaboratorRequestDTO;

public record ValidateBadgeNumberEvent(

        RegisterCollaboratorRequestDTO request
) {
}

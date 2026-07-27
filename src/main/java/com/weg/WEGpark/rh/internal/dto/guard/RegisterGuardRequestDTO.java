package com.weg.WEGpark.rh.internal.dto.guard;

import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;
import jakarta.validation.constraints.NotBlank;

public record RegisterGuardRequestDTO(

        RegisterCollaboratorRequestDTO collaboratorDefaults,

        @NotBlank(message = "The boss can not be null or blank")
        String boss

) {
}

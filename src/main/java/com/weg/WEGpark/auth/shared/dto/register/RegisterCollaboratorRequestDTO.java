package com.weg.WEGpark.auth.shared.dto.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record RegisterCollaboratorRequestDTO(

        @Valid
        RegisterAccountRequestDTO defaults,

        @Valid
        ParkUserRegisterRequestDTO parkUserDefaults,

        @NotBlank(message = "The badge number can not be null or blank")
        String badgeNumber,

        @NotBlank(message = "The location can not be null")
        String location
) {
}

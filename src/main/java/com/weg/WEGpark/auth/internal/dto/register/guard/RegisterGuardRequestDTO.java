package com.weg.WEGpark.auth.internal.dto.register.guard;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountRequestDTO;
import jakarta.validation.constraints.NotBlank;

public record RegisterGuardRequestDTO(

        RegisterAccountRequestDTO defaults,

        @NotBlank(message = "The badge number can not be null or blank")
        String badgeNumber,

        @NotBlank(message = "The location can not be null or blank")
        String location,

        @NotBlank(message = "The boss can not be null or blank")
        String boss
) {
}

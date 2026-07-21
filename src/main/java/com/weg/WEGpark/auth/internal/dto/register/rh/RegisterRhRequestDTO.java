package com.weg.WEGpark.auth.internal.dto.register.rh;

import jakarta.validation.constraints.NotBlank;

public record RegisterRhRequestDTO(

        @NotBlank(message = "The telephone can not be blank or null")
        String telephone,

        @NotBlank(message = "The name can not be blank or null")
        String name,

        @NotBlank(message = "The badge number can not blank or null")
        String badgeNumber
) {
}

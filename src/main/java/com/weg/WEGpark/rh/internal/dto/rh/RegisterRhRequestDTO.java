package com.weg.WEGpark.rh.internal.dto.rh;

import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record RegisterRhRequestDTO(

        @Valid
        RegisterAccountRequestDTO defaults,

        @NotBlank(message = "The telephone can not be blank or null")
        String telephone,

        @NotBlank(message = "The name can not be blank or null")
        String name,

        @NotBlank(message = "The badge number can not blank or null")
        String badgeNumber

) {
}

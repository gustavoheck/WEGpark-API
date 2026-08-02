package com.weg.WEGpark.auth.shared.dto.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record RegisterVisitorRequestDTO(

        @Valid
        RegisterAccountRequestDTO defaults,

        @Valid
        ParkUserRegisterRequestDTO parkUserDefaults,

        String company,

        @NotBlank(message = "The cpf can not be null or blank")
        String cpf
) {
}

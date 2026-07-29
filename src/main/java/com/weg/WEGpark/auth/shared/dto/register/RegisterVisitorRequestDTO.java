package com.weg.WEGpark.auth.shared.dto.register;

import jakarta.validation.constraints.NotBlank;

public record RegisterVisitorRequestDTO(

        RegisterAccountRequestDTO defaults,

        ParkUserRegisterRequestDTO parkUserDefaults,

        String company,

        @NotBlank(message = "The cpf can not be null or blank")
        String cpf
) {
}

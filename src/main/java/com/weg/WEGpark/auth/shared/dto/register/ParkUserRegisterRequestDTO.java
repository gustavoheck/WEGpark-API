package com.weg.WEGpark.auth.shared.dto.register;

import jakarta.validation.constraints.NotBlank;

public record ParkUserRegisterRequestDTO(

        @NotBlank(message = "The name can not be null or blank")
        String name,

        @NotBlank(message = "The telephone can not be null or blank")
        String telephone
) {
}

package com.weg.WEGpark.auth.internal.dto.defaults;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(

        @Email(message = "The email is in a wrong format")
        @NotBlank(message = "The email can not be null or blank")
        String email,

        @NotBlank(message = "Can not find a unique user without it's role")
        String role
) {
}

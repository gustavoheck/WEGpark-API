package com.weg.WEGpark.auth.internal.dto.login;

import com.weg.WEGpark.auth.internal.dto.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(

        @Email(message = "The email is in a wrong format")
        @NotBlank(message = "The email can not be null or blank")
        String email,

        @ValidPassword
        @NotBlank(message = "The email can not be null or blank")
        String password,

        String role
) {
}

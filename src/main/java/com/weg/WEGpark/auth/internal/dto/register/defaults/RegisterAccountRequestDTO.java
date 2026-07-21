package com.weg.WEGpark.auth.internal.dto.register.defaults;

import com.weg.WEGpark.auth.internal.dto.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterAccountRequestDTO(

        @Email(message = "The email is in a wrong format")
        @NotBlank(message = "The email can not be blank or null")
        String email,

        @ValidPassword
        String password
) {
}

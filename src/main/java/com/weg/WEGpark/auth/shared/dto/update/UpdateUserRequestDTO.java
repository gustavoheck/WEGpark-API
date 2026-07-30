package com.weg.WEGpark.auth.shared.dto.update;

import com.weg.WEGpark.auth.internal.dto.validation.ValidPassword;

public record UpdateUserRequestDTO(

        String email,

        String role,

        @ValidPassword
        String password,

        String actualPassword,

        String tokenIfPasswordReset
) {
}

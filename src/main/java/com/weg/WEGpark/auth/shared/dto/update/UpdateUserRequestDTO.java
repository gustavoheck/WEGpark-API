package com.weg.WEGpark.auth.shared.dto.update;

public record UpdateUserRequestDTO(

        String email,

        String role,

        String password,

        String actualPassword,

        String tokenIfPasswordReset
) {
}

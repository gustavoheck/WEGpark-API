package com.weg.WEGpark.auth.internal.app.dto;

public record UpdateUserDTO(
        String email,

        String password,

        String actualPassword
) {
}

package com.weg.WEGpark.auth.internal.dto.update;

public record UpdateUserRequestDTO(

        String email,

        String password,

        String actualPassword
) {
}

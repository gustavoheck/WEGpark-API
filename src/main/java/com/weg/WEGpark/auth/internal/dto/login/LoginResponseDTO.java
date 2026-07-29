package com.weg.WEGpark.auth.internal.dto.login;

public record LoginResponseDTO(
        Boolean authenticated,
        String message,
        String token
) {
}

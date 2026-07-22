package com.weg.WEGpark.auth.internal.dto.register.defaults;

import java.util.UUID;

public record RegisterAccountResponseDTO(
        UUID uuid,

        String email
) {
}

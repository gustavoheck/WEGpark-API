package com.weg.WEGpark.auth.shared.dto.register;

import java.util.UUID;

public record RegisterAccountResponseDTO(
        UUID uuid,

        String email
) {
}

package com.weg.WEGpark.auth.shared.dto.update;

import java.util.UUID;

public record UpdateUserResponseDTO(

        Long id,

        UUID uuid,

        String email
) {
}

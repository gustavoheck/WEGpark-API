package com.weg.WEGpark.park.shared.dto.update;

import java.util.UUID;

public record UpdateParkUserResponseDTO(

        UUID uuid,

        String email,

        String telephone,

        String name
) {
}

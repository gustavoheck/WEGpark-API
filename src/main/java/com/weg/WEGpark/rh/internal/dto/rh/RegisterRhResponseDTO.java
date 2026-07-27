package com.weg.WEGpark.rh.internal.dto.rh;

import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;

public record RegisterRhResponseDTO(
        RegisterAccountResponseDTO defaults,

        String telephone,

        String name,

        String badgeNumber
) {
}

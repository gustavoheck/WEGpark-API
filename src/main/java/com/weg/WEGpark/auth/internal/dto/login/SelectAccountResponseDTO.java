package com.weg.WEGpark.auth.internal.dto.login;

import com.weg.WEGpark.auth.internal.domain.enums.RolesType;

import java.util.List;

public record SelectAccountResponseDTO(
        List<RolesType> userRoles
) {
}

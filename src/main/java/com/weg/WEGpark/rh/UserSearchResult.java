package com.weg.WEGpark.rh;

import java.util.UUID;

public record UserSearchResult(
        Long id,
        UUID uuid,
        String email,
        String telephone,
        String name,
        String badgeNumber,
        String cpf,
        Boolean active,
        Record response
) {
}

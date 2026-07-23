package com.weg.WEGpark.auth.internal.infra.security.config;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record JWTUserData(
        UUID uuid,
        String email,
        List<String> roles
) {
}

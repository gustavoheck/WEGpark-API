package com.weg.WEGpark.auth.shared.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record JWTUserData(
        UUID uuid,
        String email,
        List<String> roles,
        String name
) {
}

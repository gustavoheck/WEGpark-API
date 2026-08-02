package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.shared.enums.TokenType;

import java.util.UUID;

public record SendAccountValidationEmailEvent(
        UUID authToken,

        String email,

        TokenType tokenType
) {
}

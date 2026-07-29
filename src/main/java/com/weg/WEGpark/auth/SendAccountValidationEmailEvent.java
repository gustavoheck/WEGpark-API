package com.weg.WEGpark.auth;

import java.util.UUID;

public record SendAccountValidationEmailEvent(

        UUID authToken,

        String email
) {
}

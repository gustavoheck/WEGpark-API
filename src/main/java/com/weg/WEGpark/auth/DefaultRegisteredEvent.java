package com.weg.WEGpark.auth;

import java.util.UUID;

public record DefaultRegisteredEvent(

        UUID uuid,

        Long id,

        String email
) {
}

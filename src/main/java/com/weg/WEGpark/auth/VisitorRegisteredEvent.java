package com.weg.WEGpark.auth;

import java.util.UUID;

public record VisitorRegisteredEvent(

        Long id,

        UUID uuid,

        String email,

        String telephone,

        String name,

        String company,

        String cpf
) {
}

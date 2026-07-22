package com.weg.WEGpark.auth;

import java.util.UUID;

public record CollaboratorRegisteredEvent(

        Long id,

        UUID uuid,

        String email,

        String telephone,

        String name,

        String badgeNumber,

        String location
) {
}
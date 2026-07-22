package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record VisitorRegisteredEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        Long id,

        UUID uuid,

        String email,

        String telephone,

        String name,

        String company,

        String cpf
) {
}

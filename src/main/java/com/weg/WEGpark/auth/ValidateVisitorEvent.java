package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;

import java.util.concurrent.CompletableFuture;

public record ValidateVisitorEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        RegisterVisitorRequestDTO request
) {
}

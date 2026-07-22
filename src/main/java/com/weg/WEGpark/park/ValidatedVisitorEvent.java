package com.weg.WEGpark.park;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterVisitorRequestDTO;

import java.util.concurrent.CompletableFuture;

public record ValidatedVisitorEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        RegisterVisitorRequestDTO request,

        Boolean exists
) {
}

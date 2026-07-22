package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;

import java.util.concurrent.CompletableFuture;

public record ValidateBadgeNumberEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        RegisterCollaboratorRequestDTO request
) {
}

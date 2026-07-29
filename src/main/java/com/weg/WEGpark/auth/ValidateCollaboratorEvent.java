package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.shared.dto.register.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;

import java.util.concurrent.CompletableFuture;

public record ValidateCollaboratorEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        RegisterCollaboratorRequestDTO request
) {
}

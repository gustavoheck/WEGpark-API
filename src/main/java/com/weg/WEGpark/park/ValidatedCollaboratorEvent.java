package com.weg.WEGpark.park;

import com.weg.WEGpark.auth.internal.dto.register.defaults.RegisterAccountResponseDTO;
import com.weg.WEGpark.auth.shared.dto.register.RegisterCollaboratorRequestDTO;

import java.util.concurrent.CompletableFuture;

public record ValidatedCollaboratorEvent(

        CompletableFuture<RegisterAccountResponseDTO> futureResponse,

        RegisterCollaboratorRequestDTO request,

        Long collaboratorId

) {
}

package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.shared.dto.update.UpdateUserResponseDTO;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record UpdateUserAuthEvent(

        CompletableFuture<UpdateUserResponseDTO> eventResponse,

        UUID targetUuid,

        String email,

        String password
) {
}

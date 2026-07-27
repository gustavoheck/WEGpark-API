package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.internal.dto.update.UpdateUserResponseDTO;

import java.util.concurrent.CompletableFuture;

public record UpdateUserAuthEvent(

        CompletableFuture<UpdateUserResponseDTO> eventResponse,

        String email,

        String password
) {
}

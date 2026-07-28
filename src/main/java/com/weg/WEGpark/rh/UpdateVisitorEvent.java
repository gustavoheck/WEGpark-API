package com.weg.WEGpark.rh;

import com.weg.WEGpark.park.shared.dto.update.UpdateVisitorResponseDTO;

import java.util.concurrent.CompletableFuture;

public record UpdateVisitorEvent(
        CompletableFuture<UpdateVisitorResponseDTO> eventResponse,

        String telephone,

        String name,

        String company,

        String cpf
) {
}

package com.weg.WEGpark.rh;

import com.weg.WEGpark.park.shared.dto.update.UpdateCollaboratorResponseDTO;

import java.util.concurrent.CompletableFuture;

public record UpdateCollaboratorEvent(

        CompletableFuture<UpdateCollaboratorResponseDTO> eventResponse,

        String telephone,

        String name,

        String badgeNumber,

        String location
) {
}

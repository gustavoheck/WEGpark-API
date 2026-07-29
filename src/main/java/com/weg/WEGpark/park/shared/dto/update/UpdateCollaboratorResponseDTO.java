package com.weg.WEGpark.park.shared.dto.update;

public record UpdateCollaboratorResponseDTO(

        UpdateParkUserResponseDTO defaults,

        String badgeNumber,

        String location
) {
}

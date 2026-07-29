package com.weg.WEGpark.park.shared.dto.update;

public record UpdateCollaboratorRequestDTO(

        UpdateParkUserRequestDTO defaults,

        String badgeNumber,

        String location
) {
}

package com.weg.WEGpark.park.internal.dto.user.collaborator;

import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;

public record GetCollaboratorResponseDTO(

        GetParkUserResponseDTO defaults,

        String badgeNumber,

        String location
) {
}

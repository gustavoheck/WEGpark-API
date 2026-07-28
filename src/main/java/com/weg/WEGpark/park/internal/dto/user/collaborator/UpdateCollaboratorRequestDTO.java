package com.weg.WEGpark.park.internal.dto.user.collaborator;

import com.weg.WEGpark.park.internal.dto.user.defaults.UpdateParkUserRequestDTO;

import java.util.UUID;

public record UpdateCollaboratorRequestDTO(

        UpdateParkUserRequestDTO defaults,

        String badgeNumber,

        String location
) {
}

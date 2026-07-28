package com.weg.WEGpark.park.shared.dto.update;

import com.weg.WEGpark.park.internal.dto.user.defaults.UpdateParkUserRequestDTO;

public record UpdateVisitorResponseDTO(

        UpdateParkUserResponseDTO defaults,

        String company,

        String cpf
) {
}

package com.weg.WEGpark.park.internal.dto.user.visitor;

import com.weg.WEGpark.park.internal.dto.user.defaults.GetParkUserResponseDTO;
import jakarta.persistence.Column;

public record GetVisitorResponseDTO (

        GetParkUserResponseDTO defaults,

        String company,

        String cpf
) {
}

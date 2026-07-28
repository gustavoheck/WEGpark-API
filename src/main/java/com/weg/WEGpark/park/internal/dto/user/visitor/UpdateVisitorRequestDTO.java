package com.weg.WEGpark.park.internal.dto.user.visitor;

import com.weg.WEGpark.park.internal.dto.user.defaults.UpdateParkUserRequestDTO;

public record UpdateVisitorRequestDTO(

        UpdateParkUserRequestDTO defaults,

        String company,

        String cpf
) {
}

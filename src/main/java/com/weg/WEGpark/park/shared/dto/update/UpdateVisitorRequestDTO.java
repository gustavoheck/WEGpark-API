package com.weg.WEGpark.park.shared.dto.update;

public record UpdateVisitorRequestDTO(

        UpdateParkUserRequestDTO defaults,

        String company,

        String cpf
) {
}

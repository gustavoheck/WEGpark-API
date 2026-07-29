package com.weg.WEGpark.park.shared.dto.update;

public record UpdateVisitorResponseDTO(

        UpdateParkUserResponseDTO defaults,

        String company,

        String cpf
) {
}

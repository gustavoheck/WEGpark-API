package com.weg.WEGpark.auth.internal.dto.reset;

import jakarta.validation.constraints.NotBlank;

public record NumberTokenVerificateTryRequestDTO(

        @NotBlank(message = "The number token id can not be null")
        String numberTokenId,

        @NotBlank(message = "The number code can not be null")
        String numberCode
) {
}

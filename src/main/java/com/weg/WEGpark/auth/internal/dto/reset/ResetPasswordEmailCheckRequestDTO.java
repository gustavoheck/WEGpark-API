package com.weg.WEGpark.auth.internal.dto.reset;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordEmailCheckRequestDTO(

        @NotBlank(message = "The token can not be null")
        String token,

        @NotBlank(message = "The number code cant not be null")
        String numberCode
) {
}

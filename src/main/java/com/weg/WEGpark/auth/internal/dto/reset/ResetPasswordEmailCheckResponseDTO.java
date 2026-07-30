package com.weg.WEGpark.auth.internal.dto.reset;

import java.util.UUID;

public record ResetPasswordEmailCheckResponseDTO(

        UUID newToken
) {
}

package com.weg.WEGpark.auth.shared.dto.update;

import java.util.UUID;

public record UpdateUserRequestDTO(

        String email,

        String password,

        String actualPassword
) {
}

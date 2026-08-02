package com.weg.WEGpark.auth.shared.infra.handler;

import java.util.List;

public record MultiCampsErrorResponseDTO(
        String timestamp,
        Integer status,
        String error,
        List<ErrorCampDTO> message,
        String path
) {
}

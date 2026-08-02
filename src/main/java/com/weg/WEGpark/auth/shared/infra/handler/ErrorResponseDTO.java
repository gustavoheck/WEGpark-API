package com.weg.WEGpark.auth.shared.infra.handler;

public record ErrorResponseDTO (
        String timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}

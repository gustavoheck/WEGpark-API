package com.weg.WEGpark.auth.internal.infra.handler;

import com.weg.WEGpark.auth.internal.app.exception.InvalidRequestException;
import com.weg.WEGpark.auth.shared.infra.handler.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthExceptionHandlerTest {

    private final AuthExceptionHandler handler = new AuthExceptionHandler();

    @Test
    void mapsInvalidRequestToBadRequest() {
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/auth/login");

        ResponseEntity<ErrorResponseDTO> response = handler.invalidRequestExceptionHandler(
                new InvalidRequestException("Invalid role value", new IllegalArgumentException()),
                request
        );

        assertNotNull(response.getBody());
        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status()),
                () -> assertEquals("Invalid role value", response.getBody().message()),
                () -> assertEquals("/auth/login", response.getBody().path())
        );
    }
}

package com.weg.WEGpark.notification.internal.infra.handler;

import com.weg.WEGpark.auth.shared.infra.handler.DefaultExceptionHandler;
import com.weg.WEGpark.auth.shared.infra.handler.ErrorResponseDTO;
import com.weg.WEGpark.notification.internal.app.notification.exception.CreatingEmailMessageErrorException;
import com.weg.WEGpark.notification.internal.app.notification.exception.ImageNotEncounteredException;
import com.weg.WEGpark.notification.internal.app.notification.exception.InvalidNotificationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice(basePackages = "com.weg.WEGpark.notification")
public class NotificationExceptionHandler extends DefaultExceptionHandler {

    @ExceptionHandler(InvalidNotificationException.class)
    public ResponseEntity<ErrorResponseDTO> invalidNotificationExceptionHandler(
            InvalidNotificationException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler({ImageNotEncounteredException.class, CreatingEmailMessageErrorException.class})
    public ResponseEntity<ErrorResponseDTO> emailCreationExceptionHandler(
            RuntimeException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, request);
    }
}

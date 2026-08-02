package com.weg.WEGpark.auth.internal.infra.handler;

import com.weg.WEGpark.auth.internal.app.exception.AccountEmailNotActiveException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidEmailValidationException;
import com.weg.WEGpark.auth.internal.app.exception.InvalidLoginException;
import com.weg.WEGpark.auth.shared.infra.handler.DefaultExceptionHandler;
import com.weg.WEGpark.auth.shared.infra.handler.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import com.weg.WEGpark.auth.internal.infra.security.exception.InvalidTokenException;

@RestControllerAdvice(basePackages = "com.weg.WEGpark.auth")
public class AuthExceptionHandler extends DefaultExceptionHandler {

    @ExceptionHandler({InvalidLoginException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponseDTO> invalidCredentialsExceptionHandler(RuntimeException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception, request);
    }

    @ExceptionHandler({
            InvalidTokenException.class
    })
    public ResponseEntity<ErrorResponseDTO> invalidTokenExceptionHandler(RuntimeException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception, request);
    }

    @ExceptionHandler(InvalidEmailValidationException.class)
    public ResponseEntity<ErrorResponseDTO> invalidEmailValidationExceptionHandler(
            InvalidEmailValidationException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception, request);
    }

    @ExceptionHandler(AccountEmailNotActiveException.class)
    public ResponseEntity<ErrorResponseDTO> accountEmailNotActiveExceptionHandler(
            AccountEmailNotActiveException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception, request);
    }
}

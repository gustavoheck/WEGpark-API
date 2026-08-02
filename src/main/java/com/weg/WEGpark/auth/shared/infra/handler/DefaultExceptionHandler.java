package com.weg.WEGpark.auth.shared.infra.handler;

import com.weg.WEGpark.auth.shared.exception.AlreadyHaveAccountException;
import com.weg.WEGpark.shared.exception.MoreThenOneFilterException;
import com.weg.WEGpark.shared.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.List;

public abstract class DefaultExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> httpMessageNotReadableHandler (HttpMessageNotReadableException exception, WebRequest request) {

        return buildErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT, exception, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notFoundExceptionHandler(NotFoundException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(MoreThenOneFilterException.class)
    public ResponseEntity<ErrorResponseDTO> moreThenOneFilterExceptionHandler(MoreThenOneFilterException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception, request);
    }

    @ExceptionHandler(AlreadyHaveAccountException.class)
    public ResponseEntity<ErrorResponseDTO> alreadyHaveAccountExceptionHandler(AlreadyHaveAccountException exception, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception, request);
    }

    protected ResponseEntity<ErrorResponseDTO> buildErrorResponse(HttpStatus status, RuntimeException exception, WebRequest request) {

        String path = request.getDescription(false).replaceAll("uri=", "");

        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(
                        Instant.now().toString(),
                        status.value(),
                        status.toString(),
                        exception.getMessage(),
                        path
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MultiCampsErrorResponseDTO> validationExceptionHandler (MethodArgumentNotValidException exception, WebRequest request) {

        String path = request.getDescription(false).replaceAll("uri=", "");

        List<ErrorCampDTO> errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map((fieldError) -> new ErrorCampDTO(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MultiCampsErrorResponseDTO(
                        Instant.now().toString(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.toString(),
                        errorMessage,
                        path
                ));
    }

}

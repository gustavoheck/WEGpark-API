package com.weg.WEGpark.auth.shared.infra.handler;

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
    private ResponseEntity<ErrorResponseDTO> httpMessageNotReadableHandler (HttpMessageNotReadableException exception, WebRequest request) {

        String path = request.getDescription(false).replaceAll("uri=", "");

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(new ErrorResponseDTO(
                        Instant.now().toString(),
                        HttpStatus.UNPROCESSABLE_CONTENT.value(),
                        HttpStatus.UNPROCESSABLE_CONTENT.toString(),
                        exception.getMessage(),
                        path
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<MultiCampsErrorResponseDTO> validationExceptionHandler (MethodArgumentNotValidException exception, WebRequest request) {

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
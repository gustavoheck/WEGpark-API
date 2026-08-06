package com.weg.WEGpark.park.internal.infra.handler;

import com.weg.WEGpark.auth.shared.infra.handler.DefaultExceptionHandler;
import com.weg.WEGpark.auth.shared.infra.handler.ErrorResponseDTO;
import com.weg.WEGpark.park.internal.app.vehicle.exception.NotificationNotFoundException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyAssociatedWithUserException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyOwnedByUserException;
import com.weg.WEGpark.park.internal.app.vehicle.exception.VehicleAlreadyRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice(basePackages = "com.weg.WEGpark.park")
public class ParkExceptionHandler extends DefaultExceptionHandler {

    @ExceptionHandler(VehicleAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponseDTO> vehicleAlreadyRegisteredExceptionHandler(
            VehicleAlreadyRegisteredException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception, request);
    }

    @ExceptionHandler({
            VehicleAlreadyOwnedByUserException.class,
            VehicleAlreadyAssociatedWithUserException.class
    })
    public ResponseEntity<ErrorResponseDTO> vehicleUserConflictExceptionHandler(
            RuntimeException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.CONFLICT, exception, request);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> notificationNotFoundExceptionHandler(
            NotificationNotFoundException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDTO> illegalStateExceptionHandler(
            IllegalStateException exception,
            WebRequest request
    ) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception, request);
    }
}

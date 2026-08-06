package com.weg.WEGpark.park.internal.app.vehicle.exception;

public class VehicleAlreadyOwnedByUserException extends RuntimeException {
    public VehicleAlreadyOwnedByUserException(String message) {
        super(message);
    }
}

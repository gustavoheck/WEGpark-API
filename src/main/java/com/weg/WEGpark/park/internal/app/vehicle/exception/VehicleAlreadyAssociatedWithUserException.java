package com.weg.WEGpark.park.internal.app.vehicle.exception;

public class VehicleAlreadyAssociatedWithUserException extends RuntimeException {
    public VehicleAlreadyAssociatedWithUserException(String message) {
        super(message);
    }
}

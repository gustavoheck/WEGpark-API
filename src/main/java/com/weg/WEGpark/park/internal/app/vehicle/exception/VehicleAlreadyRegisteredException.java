package com.weg.WEGpark.park.internal.app.vehicle.exception;

public class VehicleAlreadyRegisteredException extends RuntimeException {
    public VehicleAlreadyRegisteredException(String message) {
        super(message);
    }
}

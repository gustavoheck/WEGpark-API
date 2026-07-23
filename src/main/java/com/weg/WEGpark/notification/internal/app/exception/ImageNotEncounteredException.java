package com.weg.WEGpark.notification.internal.app.exception;

public class ImageNotEncounteredException extends RuntimeException {
    public ImageNotEncounteredException() {
        super("The logo image for email sending was not encountered");
    }
}

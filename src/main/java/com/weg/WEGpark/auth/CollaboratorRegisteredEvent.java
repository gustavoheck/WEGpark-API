package com.weg.WEGpark.auth;

public record CollaboratorRegisteredEvent(

        String badgeNumber,

        String location
) {
}
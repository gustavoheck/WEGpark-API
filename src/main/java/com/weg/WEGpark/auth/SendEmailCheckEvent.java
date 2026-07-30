package com.weg.WEGpark.auth;

public record SendEmailCheckEvent(

        String email,

        String number
) {
}

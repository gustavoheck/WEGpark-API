package com.weg.WEGpark.auth.internal.domain.enums;

public enum RolesType {
    GUARD(3),
    RH(3),
    ADMIN(3),
    PARK(1);

    private final int tokenExpirationTime;

    RolesType(int tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public int getTokenExpirationTime() {
        return this.tokenExpirationTime;
    }
}

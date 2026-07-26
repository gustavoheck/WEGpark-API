package com.weg.WEGpark.auth.internal.domain.enums;

public enum RolesType {
    ROLE_GUARD(3),
    ROLE_RH(3),
    ROLE_ADMIN(3),
    ROLE_PARK(1);

    private final int tokenExpirationTime;

    RolesType(int tokenExpirationTime) {
        this.tokenExpirationTime = tokenExpirationTime;
    }

    public int getTokenExpirationTime() {
        return this.tokenExpirationTime;
    }
}

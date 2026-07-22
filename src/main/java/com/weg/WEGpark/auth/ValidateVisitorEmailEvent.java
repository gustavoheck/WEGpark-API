package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.internal.dto.visitor.RegisterVisitorRequestDTO;

public record ValidateVisitorEmailEvent(

        RegisterVisitorRequestDTO request
) {
}

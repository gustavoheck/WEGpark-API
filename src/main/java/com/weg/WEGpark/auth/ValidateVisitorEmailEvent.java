package com.weg.WEGpark.auth;

import com.weg.WEGpark.auth.shared.dto.visitor.RegisterVisitorRequestDTO;

public record ValidateVisitorEmailEvent(

        RegisterVisitorRequestDTO request
) {
}

package com.weg.WEGpark.park;

import com.weg.WEGpark.auth.shared.dto.visitor.RegisterVisitorRequestDTO;

public record ValidatedVisitorEvent(

        RegisterVisitorRequestDTO request,

        Boolean exists
) {
}

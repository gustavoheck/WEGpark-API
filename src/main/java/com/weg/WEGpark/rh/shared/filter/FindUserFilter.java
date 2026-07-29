package com.weg.WEGpark.rh.shared.filter;

public record FindUserFilter(

        String name,

        String badgeNumber,

        String cpf,

        Boolean active
) {
}

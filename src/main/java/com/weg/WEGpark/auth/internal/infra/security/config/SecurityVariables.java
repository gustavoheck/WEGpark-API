package com.weg.WEGpark.auth.internal.infra.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.variables")
public record SecurityVariables(
        String privateKey,
        String publicKey
) {
}

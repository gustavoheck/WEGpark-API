package com.weg.WEGpark.notification.internal.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wegpark.config")
public record EmailVariables(

        String url
) { }

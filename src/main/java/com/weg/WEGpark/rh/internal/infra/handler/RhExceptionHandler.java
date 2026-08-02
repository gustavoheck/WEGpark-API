package com.weg.WEGpark.rh.internal.infra.handler;

import com.weg.WEGpark.auth.shared.infra.handler.DefaultExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.weg.WEGpark.rh")
public class RhExceptionHandler extends DefaultExceptionHandler {
}

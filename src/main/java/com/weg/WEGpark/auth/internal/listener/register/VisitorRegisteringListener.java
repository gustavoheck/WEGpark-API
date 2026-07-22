package com.weg.WEGpark.auth.internal.listener.register;

import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorRegisteringListener {

    private final RegisterService registerService;


}

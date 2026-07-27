package com.weg.WEGpark.auth.internal.listener.register;

import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.rh.RegisterRhEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestRegisteringListener {

    private final RegisterService registerService;

    @EventListener
    public void registerRh (RegisterRhEvent event) {
        registerService.registerRhAccount(event);
    }
}

package com.weg.WEGpark.auth.internal.listener.register;

import com.weg.WEGpark.auth.internal.app.service.RegisterService;
import com.weg.WEGpark.park.ValidatedVisitorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorRegisteringListener {

    private final RegisterService registerService;

    @EventListener
    public void startRegistering (ValidatedVisitorEvent event) {
        registerService.registerVisitor(event.futureResponse(), event.request(), event.exists());
    }
}

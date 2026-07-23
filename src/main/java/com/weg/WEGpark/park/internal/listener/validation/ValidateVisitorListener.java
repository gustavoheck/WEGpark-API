package com.weg.WEGpark.park.internal.listener.validation;

import com.weg.WEGpark.auth.ValidateVisitorEvent;
import com.weg.WEGpark.park.ValidatedVisitorEvent;
import com.weg.WEGpark.park.internal.app.user.service.ParkUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidateVisitorListener {

    private final ParkUserService parkUserService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @EventListener
    public void validateVisitor (ValidateVisitorEvent event) {
        Boolean existsByEmail = parkUserService.verifyParkUserToRegister(event.request().defaults().email());
        applicationEventPublisher.publishEvent(new ValidatedVisitorEvent(event.futureResponse(), event.request(), existsByEmail));
    }
}

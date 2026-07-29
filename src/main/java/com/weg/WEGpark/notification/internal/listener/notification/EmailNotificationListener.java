package com.weg.WEGpark.notification.internal.listener.notification;

import com.weg.WEGpark.auth.SendAccountValidationEmailEvent;
import com.weg.WEGpark.notification.internal.app.notification.service.EmailService;
import com.weg.WEGpark.notification.internal.dto.EmailVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationListener {

    private final EmailService emailService;
    private final EmailVariables emailVariables;

    @Async("asyncTaskExecutor")
    @EventListener
    public void sendValidationEmail (SendAccountValidationEmailEvent event) {

        emailService.sendNotification(
                event.email(),
                "Valide sua conta no WEGPark!",
                "Você precisa validar sua conta para usar o sistema do WEGPark!",
                true,
                "%s/auth/validate-email/%s".formatted(emailVariables.url(), event.authToken()),
                "Validar Conta"
        );
    }
}

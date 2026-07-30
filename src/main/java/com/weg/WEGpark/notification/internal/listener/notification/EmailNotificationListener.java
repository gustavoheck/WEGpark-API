package com.weg.WEGpark.notification.internal.listener.notification;

import com.weg.WEGpark.auth.SendAccountValidationEmailEvent;
import com.weg.WEGpark.auth.SendEmailCheckEvent;
import com.weg.WEGpark.notification.internal.app.notification.service.EmailService;
import com.weg.WEGpark.notification.internal.dto.EmailVariables;
import com.weg.WEGpark.park.SendOccurrenceNotificationEvent;
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
                null,
                event.email(),
                "Valide sua conta no WEGPark!",
                "Você precisa validar sua conta para usar o sistema do WEGPark!",
                true,
                "%s/auth/validate-email/%s".formatted(emailVariables.url(), event.authToken()),
                "Validar Conta"
        );
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void sendEmailCheck (SendEmailCheckEvent event) {

        emailService.sendNotification(
                null,
                event.email(),
                "Codigo de verificação",
                "Veja seu código de verificação abaixo!<br><p style=\"font-size:40px\">" + event.number() + "</p>",
                false,
                null,
                null
        );
    }

    @Async("asyncTaskExecutor")
    @EventListener
    public void sendOccurrenceNotification (SendOccurrenceNotificationEvent event) {

        emailService.sendNotification(
                event.nome(),
                event.email(),
                "Nova ocorrência registrada no seu nome.",
                "Identificamos uma ocorrência vinculada à sua conta no WEGpark.<br>Clique no botão abaixo para conferir os detalhes e resolver o quanto antes.",
                true,
                "%s/ocorrencias/%s".formatted(emailVariables.websiteUrl(), event.uuid()),
                "Ver Ocorrência"
        );
    }
}

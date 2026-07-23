package com.weg.WEGpark.notification.internal.app.service.email;

import java.nio.charset.StandardCharsets;

import com.weg.WEGpark.notification.internal.app.exception.ImageNotEncounteredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class NotificationEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendNotification(
            String destinationEmail,
            String userName,
            String subject,
            String warningMessage,
            Boolean useButton,
            String buttonUrl,
            String buttonText
    ) throws MessagingException {

        String htmlContent;

        Context context = new Context();
        context.setVariable("isEmail", true);
        context.setVariable("assunto", subject);
        context.setVariable("resumoNotificacao", warningMessage);
        context.setVariable("nomeSistema", "WEGpark");
        context.setVariable("urlSistema", "https://wegpark.com.br");
        context.setVariable("nomeUsuario", userName);
        context.setVariable("mensagemAviso", warningMessage);
        if (useButton == null || useButton) {
            context.setVariable("urlAcao", buttonUrl);
            context.setVariable("textoBotao", buttonText != null ? buttonText : "Ver Notificação");
            htmlContent = templateEngine.process("email-template-button", context);
        } else {
            htmlContent = templateEngine.process("email-template", context);
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(destinationEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        try {
            helper.addInline("logoWegpark", new ClassPathResource("static/images/logo-white.png"));
        } catch (Exception e) {
            throw new ImageNotEncounteredException();
        }

        mailSender.send(message);
    }
}

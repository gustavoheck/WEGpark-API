package com.weg.WEGpark.notification.internal.app.service;

import java.nio.charset.StandardCharsets;

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
            String warningMessage
    ) throws MessagingException {

        Context context = new Context();
        context.setVariable("isEmail", true);
        context.setVariable("assunto", subject);
        context.setVariable("resumoNotificacao", warningMessage);
        context.setVariable("nomeSistema", "WEGpark");
        context.setVariable("urlSistema", "https://wegpark.com.br");
        context.setVariable("nomeUsuario", userName);
        context.setVariable("mensagemAviso", warningMessage);

        String htmlContent = templateEngine.process("index", context);

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
            System.err.println("Aviso: Imagem da logo não encontrada em static/images/logo.png");
        }

        mailSender.send(message);
    }
}

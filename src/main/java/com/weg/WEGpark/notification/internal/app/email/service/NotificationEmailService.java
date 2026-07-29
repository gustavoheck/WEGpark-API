package com.weg.WEGpark.notification.internal.app.email.service;

import java.nio.charset.StandardCharsets;

import com.weg.WEGpark.notification.internal.app.exception.CreatingEmailMessageErrorException;
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
    ) {

        String htmlContent;

        Context context = new Context();
        context.setVariable("isEmail", true);
        context.setVariable("subject", subject);
        context.setVariable("notificationSummary", warningMessage);
        context.setVariable("systemName", "WEGpark");
        context.setVariable("systemUrl", "https://wegpark.com.br");
        context.setVariable("userName", userName);
        context.setVariable("noticeMessage", warningMessage);

        if (useButton == null || useButton) {
            context.setVariable("urlAcao", buttonUrl);
            context.setVariable("textoBotao", buttonText != null ? buttonText : "Ver Notificação");
            htmlContent = templateEngine.process("email-template-button", context);
        } else {
            htmlContent = templateEngine.process("email-template", context);
        }

        try {
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
        } catch (MessagingException e) {
            throw new CreatingEmailMessageErrorException("Error trying to create message");
        }
    }
}

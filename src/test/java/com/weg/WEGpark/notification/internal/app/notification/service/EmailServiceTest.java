package com.weg.WEGpark.notification.internal.app.notification.service;

import com.weg.WEGpark.notification.internal.dto.EmailVariables;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {
    private JavaMailSender sender;
    private TemplateEngine templateEngine;
    private EmailService service;

    @BeforeEach
    void setUp() {
        sender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        service = new EmailService(sender, templateEngine, new EmailVariables("http://localhost", "http://localhost:3000"));
        when(sender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));
        when(templateEngine.process(anyString(), any(org.thymeleaf.context.IContext.class))).thenReturn("<html>mail</html>");
    }

    @Test
    void sendsTemplateWithoutButton() {
        service.sendNotification("User", "user@weg.net", "Subject", "Message", false, null, null);
        verify(templateEngine).process(eq("email-template"), any());
        verify(sender).send(any(MimeMessage.class));
    }

    @Test
    void sendsTemplateWithButtonAndDefaultText() {
        service.sendNotification("User", "user@weg.net", "Subject", "Message", true, "http://localhost/action", null);
        verify(templateEngine).process(eq("email-template-button"), any());
        verify(sender).send(any(MimeMessage.class));
    }
}

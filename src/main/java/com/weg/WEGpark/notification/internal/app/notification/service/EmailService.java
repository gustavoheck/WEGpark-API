package com.weg.WEGpark.notification.internal.app.notification.service;

import java.nio.charset.StandardCharsets;

import com.weg.WEGpark.notification.internal.app.notification.exception.CreatingEmailMessageErrorException;
import com.weg.WEGpark.notification.internal.app.notification.exception.ImageNotEncounteredException;
import com.weg.WEGpark.notification.internal.dto.EmailVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final EmailVariables emailVariables;

    public void sendNotification(
            String destinationEmail,
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
        context.setVariable("systemUrl", emailVariables.url());
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

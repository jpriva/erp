package com.jpriva.erpsp.notification.infra.out.smtp;

import com.jpriva.erpsp.notification.domain.constants.NotificationErrorCode;
import com.jpriva.erpsp.notification.domain.exceptions.ErpNotificationException;
import com.jpriva.erpsp.notification.domain.port.out.EmailPort;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements EmailPort {

    private final JavaMailSender mailSender;
    private final LoggerPort log;


    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Override
    public void sendEmail(String recipient, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(fromAddress, fromName);
            helper.setTo(recipient);
            helper.setSubject(subject);

            helper.setText(body, true);
            mailSender.send(message);

            log.debug("Email sent successfully to: {}", recipient);

        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Error sending email to: {}", recipient, e);
            throw new ErpNotificationException(NotificationErrorCode.EMAIL_NOTIFICATION_NOT_SENT, e);
        }
    }
}

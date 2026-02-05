package com.jpriva.erpsp.notification.application.usecases;

import com.jpriva.erpsp.notification.domain.exceptions.ErpNotificationException;
import com.jpriva.erpsp.notification.domain.model.Notification;
import com.jpriva.erpsp.notification.domain.port.out.AuthTemplatePort;
import com.jpriva.erpsp.notification.domain.port.out.EmailPort;
import com.jpriva.erpsp.notification.domain.port.out.NotificationRepositoryPort;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;

import static com.jpriva.erpsp.notification.domain.constants.NotificationErrorCode.EMAIL_NOTIFICATION_NOT_SENT;

public class SendAuthNotificationUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final AuthTemplatePort authTemplatePort;
    private final EmailPort emailPort;
    private final LoggerPort log;
    private final TransactionalPort transactional;

    public SendAuthNotificationUseCase(NotificationRepositoryPort notificationRepository, AuthTemplatePort authTemplatePort, EmailPort emailPort, LoggerPort log, TransactionalPort transactional) {
        this.notificationRepository = notificationRepository;
        this.authTemplatePort = authTemplatePort;
        this.emailPort = emailPort;
        this.log = log;
        this.transactional = transactional;
    }

    public void sendVerifyEmail(VerifyUserEmail payload) {
        log.debug("Sending email: {}", payload.email());

        String subject = "Verify Email";
        String body = authTemplatePort.generateVerificationEmail(payload);

        Notification notification = transactional.execute(() -> {
            Notification newNotif = Notification.createEmail(
                    new UserId(payload.userId()),
                    payload.email(),
                    subject,
                    body
            );
            return notificationRepository.save(newNotif);
        });

        try {
            emailPort.sendEmail(payload.email(), subject, body);

            transactional.execute(() -> {
                notification.markAsSent();
                notificationRepository.save(notification);
            });

        } catch (Exception e) {
            log.error("Sending email failed: {}", e.getMessage());

            transactional.execute(() -> {
                notification.markAsFailed(e.getMessage());
                notificationRepository.save(notification);
            });

            throw new ErpNotificationException(EMAIL_NOTIFICATION_NOT_SENT, e);
        }
    }
}

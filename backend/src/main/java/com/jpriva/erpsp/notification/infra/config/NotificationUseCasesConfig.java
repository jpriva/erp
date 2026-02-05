package com.jpriva.erpsp.notification.infra.config;

import com.jpriva.erpsp.notification.application.usecases.SendAuthNotificationUseCase;
import com.jpriva.erpsp.notification.domain.port.out.AuthTemplatePort;
import com.jpriva.erpsp.notification.domain.port.out.EmailPort;
import com.jpriva.erpsp.notification.domain.port.out.NotificationRepositoryPort;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationUseCasesConfig {
    @Bean
    public SendAuthNotificationUseCase sendAuthNotificationUseCase(
            NotificationRepositoryPort notificationRepository,
            AuthTemplatePort authTemplatePort,
            EmailPort emailPort,
            LoggerPort log,
            TransactionalPort transactional
    ) {
        return new SendAuthNotificationUseCase(
                notificationRepository,
                authTemplatePort,
                emailPort,
                log,
                transactional
        );
    }
}

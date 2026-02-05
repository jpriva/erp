package com.jpriva.erpsp.notification.infra.in.events;

import com.jpriva.erpsp.notification.application.usecases.SendAuthNotificationUseCase;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuthNotificationEvents {

    private final SendAuthNotificationUseCase sendAuthNotificationUseCase;
    private final LoggerPort log;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleRegisterUserEvent(VerifyUserEmail event) {
        log.debug("Received event: VerifyUserEmail {}", event.email());
        sendAuthNotificationUseCase.sendVerifyEmail(event);
    }
}

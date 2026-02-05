package com.jpriva.erpsp.auth.infra.out.springevents;

import com.jpriva.erpsp.auth.domain.ports.out.AuthEventPort;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEventPublisherAdapter implements AuthEventPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishRegisterUserEvent(VerifyUserEmail event) {
        applicationEventPublisher.publishEvent(event);
    }
}

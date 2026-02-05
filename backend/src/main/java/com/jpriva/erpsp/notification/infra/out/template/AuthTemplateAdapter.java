package com.jpriva.erpsp.notification.infra.out.template;

import com.jpriva.erpsp.notification.domain.port.out.AuthTemplatePort;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Locale;

import static com.jpriva.erpsp.shared.domain.utils.AppLocalesUtils.resolveSafeLocale;

@Component
@RequiredArgsConstructor
public class AuthTemplateAdapter implements AuthTemplatePort {

    private final SpringTemplateEngine templateEngine;
    private final LoggerPort log;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public String generateVerificationEmail(VerifyUserEmail payload) {
        log.debug("Generating verification email for {}", payload.email());
        String link = frontendUrl + "/verify-email/" + payload.token();
        log.debug("Link: {}", link);
        Locale locale = resolveSafeLocale(payload.language());
        log.debug("Locale: {}", locale);
        Context context = new Context(locale);
        context.setVariable("name", payload.name());
        context.setVariable("link", link);
        return templateEngine.process(TemplateNames.VERIFY_EMAIL_TEMPLATE, context);
    }
}

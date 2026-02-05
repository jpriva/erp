package com.jpriva.erpsp.notification.infra.out.template;

import com.jpriva.erpsp.config.JsonI18nConfig;
import com.jpriva.erpsp.notification.domain.port.out.AuthTemplatePort;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.utils.FakeLogger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        AuthTemplateAdapter.class,
        ThymeleafAutoConfiguration.class,
        JsonI18nConfig.class,
        AuthTemplateIntegrationTest.LoggerConfig.class
})
public class AuthTemplateIntegrationTest {
    @Autowired
    private AuthTemplatePort authTemplatePort;

    @Autowired
    private LoggerPort log;

    @TestConfiguration
    static class LoggerConfig {
        @Bean
        public LoggerPort loggerPort() {
            return Mockito.spy(new FakeLogger());
        }
    }

    @Test
    void shouldRenderEmailInEnglish() {
        VerifyUserEmail payload = new VerifyUserEmail("en", UUID.randomUUID(), "john@doe.com", "John Doe", Instant.now(), "1234");

        String htmlContent = authTemplatePort.generateVerificationEmail(payload);

        assertThat(htmlContent).contains("Welcome");
        assertThat(htmlContent).contains("https://test-url.com/verify-email/1234");
    }

    @Test
    void shouldRenderEmailInSpanish() {
        VerifyUserEmail payload = new VerifyUserEmail("es", UUID.randomUUID(), "john@doe.com", "John Doe", Instant.now(), "1234");

        String htmlContent = authTemplatePort.generateVerificationEmail(payload);

        assertThat(htmlContent).contains("Bienvenido");
        assertThat(htmlContent).contains("https://test-url.com/verify-email/1234");
    }
}

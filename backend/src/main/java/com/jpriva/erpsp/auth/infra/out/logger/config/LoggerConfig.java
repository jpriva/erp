package com.jpriva.erpsp.auth.infra.out.logger.config;

import com.jpriva.erpsp.auth.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.auth.infra.out.logger.Slf4jLoggerAdapter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggerConfig {

    @Bean
    @Scope("prototype")
    public LoggerPort loggerPort(InjectionPoint injectionPoint) {
        Class<?> targetClass = injectionPoint.getMember().getDeclaringClass();
        return new Slf4jLoggerAdapter(targetClass);
    }

}

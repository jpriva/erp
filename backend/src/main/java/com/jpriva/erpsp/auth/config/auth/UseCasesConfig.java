package com.jpriva.erpsp.auth.config.auth;

import com.jpriva.erpsp.auth.application.usecases.RegisterLocalUserUseCase;
import com.jpriva.erpsp.auth.domain.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    @Bean
    public RegisterLocalUserUseCase registerLocalUserUseCase(
            UserRepositoryPort userRepository,
            CredentialRepositoryPort credentialRepository,
            TransactionalPort transactional,
            LoggerPort logger,
            PasswordHasherPort passwordHasher
    ) {
        return new RegisterLocalUserUseCase(userRepository, credentialRepository, transactional, logger, passwordHasher);
    }
}

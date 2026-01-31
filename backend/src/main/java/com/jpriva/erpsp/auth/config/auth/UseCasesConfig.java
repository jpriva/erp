package com.jpriva.erpsp.auth.config.auth;

import com.jpriva.erpsp.auth.application.usecases.ExchangeTokenUseCase;
import com.jpriva.erpsp.auth.application.usecases.LoginLocalUserUseCase;
import com.jpriva.erpsp.auth.application.usecases.RefreshTokenUseCase;
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

    @Bean
    public LoginLocalUserUseCase loginLocalUserUseCase(
            TransactionalPort transactional,
            UserRepositoryPort userRepository,
            CredentialRepositoryPort credentialRepository,
            PasswordHasherPort passwordHasher,
            TokenHandlerPort tokenHandler
    ) {
        return new LoginLocalUserUseCase(transactional, userRepository, credentialRepository, passwordHasher, tokenHandler);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            TransactionalPort transactional,
            UserRepositoryPort userRepository,
            TokenHandlerPort tokenHandler
    ) {
        return new RefreshTokenUseCase(transactional, userRepository, tokenHandler);
    }

    @Bean
    public ExchangeTokenUseCase exchangeTokenUseCase(
            TransactionalPort transactional,
            UserRepositoryPort userRepository,
            TenantMembershipRepositoryPort membershipRepository,
            TokenHandlerPort tokenHandler
    ) {
        return new ExchangeTokenUseCase(transactional, userRepository, membershipRepository, tokenHandler);
    }
}

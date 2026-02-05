package com.jpriva.erpsp.auth.infra.config;

import com.jpriva.erpsp.auth.application.usecases.*;
import com.jpriva.erpsp.auth.domain.ports.out.*;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TokenHandlerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;
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
            PasswordHasherPort passwordHasher,
            AuthEventPort eventPort,
            VerificationTokenRepositoryPort verificationTokenRepository
    ) {
        return new RegisterLocalUserUseCase(
                userRepository,
                credentialRepository,
                transactional,
                logger,
                passwordHasher,
                eventPort,
                verificationTokenRepository
        );
    }

    @Bean
    public SendEmailVerificationUseCase sendEmailVerificationUseCase(
            UserRepositoryPort userRepository,
            VerificationTokenRepositoryPort verificationTokenRepository,
            AuthEventPort eventPort,
            LoggerPort log,
            TransactionalPort transactional
    ) {
        return new SendEmailVerificationUseCase(userRepository, verificationTokenRepository, eventPort, log, transactional);
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(
            VerificationTokenRepositoryPort verificationTokenRepository,
            UserRepositoryPort userRepository,
            LoggerPort log,
            TransactionalPort transactional
    ) {
        return new VerifyEmailUseCase(verificationTokenRepository, log, transactional, userRepository);
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

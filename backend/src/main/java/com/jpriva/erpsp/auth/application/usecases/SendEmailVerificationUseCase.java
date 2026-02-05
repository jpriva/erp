package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.shared.domain.model.token.verification.VerificationToken;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.AuthEventPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.VerificationTokenRepositoryPort;
import com.jpriva.erpsp.shared.domain.events.VerifyUserEmail;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;

import java.time.Instant;

public class SendEmailVerificationUseCase {
    private final UserRepositoryPort userRepository;
    private final VerificationTokenRepositoryPort verificationTokenRepository;
    private final AuthEventPort eventPort;
    private final LoggerPort log;
    private final TransactionalPort transactional;

    public SendEmailVerificationUseCase(
            UserRepositoryPort userRepository,
            VerificationTokenRepositoryPort verificationTokenRepository,
            AuthEventPort eventPort,
            LoggerPort log,
            TransactionalPort transactional
    ) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPort = eventPort;
        this.log = log;
        this.transactional = transactional;
    }

    public void execute(String emailStr, String language) {
        log.debug("Requesting new email verification for: {}", emailStr);
        transactional.execute(() -> {
            Email email = new Email(emailStr);
            User user = userRepository.findByEmail(email).orElseThrow(() -> {
                log.warn("User not found for email verification: {}", emailStr);
                return new ErpAuthException(AuthErrorCode.USER_NOT_FOUND);
            });

            if (user.isActive()) {
                log.warn("User already verified: {}", emailStr);
                throw new ErpAuthException(AuthErrorCode.USER_ALREADY_VERIFIED);
            }

            if (user.isBlocked()) {
                log.warn("User is blocked: {}", emailStr);
                throw new ErpAuthException(AuthErrorCode.USER_BLOCKED);
            }

            if (!user.isVerifiable()) {
                log.warn("User is not verifiable: {}", emailStr);
                throw new ErpAuthException(AuthErrorCode.USER_NOT_VERIFIABLE);
            }

            verificationTokenRepository.deleteByUserId(user.getUserId().value());

            VerificationToken verificationToken = VerificationToken.create(user.getUserId());
            verificationTokenRepository.save(verificationToken);
            log.debug("New verification token created for user: {}", emailStr);

            VerifyUserEmail verifyEmail = new VerifyUserEmail(
                    language,
                    user.getUserId().value(),
                    email.value(),
                    user.getName().fullName(),
                    Instant.now(),
                    verificationToken.getId().toString()
            );

            log.debug("Publishing resend verification email event: {}", verifyEmail);
            eventPort.publishRegisterUserEvent(verifyEmail);
        });
    }

}

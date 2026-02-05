package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.model.user.UserStatus;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.VerificationTokenRepositoryPort;
import com.jpriva.erpsp.shared.domain.model.token.verification.VerificationToken;
import com.jpriva.erpsp.shared.domain.ports.out.LoggerPort;
import com.jpriva.erpsp.shared.domain.ports.out.TransactionalPort;

import java.util.UUID;

public class VerifyEmailUseCase {

    private final VerificationTokenRepositoryPort verificationTokenRepository;
    private final LoggerPort log;
    private final TransactionalPort transactional;
    private final UserRepositoryPort userRepository;

    public VerifyEmailUseCase(
            VerificationTokenRepositoryPort verificationTokenRepository,
            LoggerPort log,
            TransactionalPort transactional,
            UserRepositoryPort userRepository
    ) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.log = log;
        this.transactional = transactional;
        this.userRepository = userRepository;
    }

    public void execute(String token) {
        transactional.execute(() -> {
            log.debug("Verifying email token: {}", token);
            UUID tokenCode = UUID.fromString(token);
            VerificationToken verificationToken = verificationTokenRepository.findByToken(tokenCode).orElseThrow(() -> {
                log.warn("Invalid email token: {}", token);
                return new ErpAuthException(AuthErrorCode.TOKEN_INVALID);
            });
            if (verificationToken.isExpired()) {
                log.warn("Expired email token: {}", token);
                throw new ErpAuthException(AuthErrorCode.TOKEN_EXPIRED);
            }
            User user = userRepository.findById(verificationToken.getUserId()).orElseThrow(() -> {
                log.error("User not found for token: {}", token);
                return new ErpAuthException(AuthErrorCode.USER_NOT_FOUND);
            });

            user.changeStatus(UserStatus.ACTIVE);
            verificationTokenRepository.deleteByToken(verificationToken.getId());
            userRepository.save(user);
            log.info("Email verified successfully for user: {}", user.getEmail());
        });

    }
}

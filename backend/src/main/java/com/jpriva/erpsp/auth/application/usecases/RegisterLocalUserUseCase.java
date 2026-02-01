package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.RegisterUserCommand;
import com.jpriva.erpsp.auth.application.dto.UserView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.credential.PasswordCredential;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.*;
import com.jpriva.erpsp.shared.domain.model.Email;

public class RegisterLocalUserUseCase {

    private final UserRepositoryPort userRepository;
    private final CredentialRepositoryPort credentialRepository;
    private final TransactionalPort transactional;
    private final LoggerPort log;
    private final PasswordHasherPort passwordHasher;

    public RegisterLocalUserUseCase(
            UserRepositoryPort userRepository,
            CredentialRepositoryPort credentialRepository,
            TransactionalPort transactional,
            LoggerPort log,
            PasswordHasherPort passwordHasher
    ) {
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.transactional = transactional;
        this.log = log;
        this.passwordHasher = passwordHasher;
    }

    public UserView execute(RegisterUserCommand cmd, String rawPassword) {
        log.debug("Registering user: {}", cmd.email());
        try {
            return transactional.execute(() -> {
                Email email = new Email(cmd.email());
                if (userRepository.existsByEmail(email)) {
                    log.warn("User already exists: {}", email.toString());
                    throw new ErpAuthException(AuthErrorCode.USER_ALREADY_EXISTS, email.toString());
                }
                User user = cmd.createUser();
                userRepository.save(user);
                log.debug("User registered: {}", email.toString());
                PasswordCredential credential = PasswordCredential.create(user.getUserId(), rawPassword, passwordHasher);
                credentialRepository.save(credential);
                log.debug("Password credential created for user: {}", email.toString());
                return UserView.from(user);
            });
        } catch (Exception e) {
            log.error("Error registering user: {}", cmd.email(), e);
            throw e;
        }
    }
}

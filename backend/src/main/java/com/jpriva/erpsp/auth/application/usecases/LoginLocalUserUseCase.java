package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.LocalLoginCommand;
import com.jpriva.erpsp.auth.application.dto.TokenView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.credential.PasswordCredential;
import com.jpriva.erpsp.auth.domain.model.token.TokenPair;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.CredentialRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.auth.domain.ports.out.TokenHandlerPort;
import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.shared.domain.model.Email;

public class LoginLocalUserUseCase {

    private final TransactionalPort transactionalPort;
    private final UserRepositoryPort userRepository;
    private final CredentialRepositoryPort credentialRepository;
    private final PasswordHasherPort passwordHasher;
    private final TokenHandlerPort tokenHandler;

    public LoginLocalUserUseCase(
            TransactionalPort transactionalPort,
            UserRepositoryPort userRepository,
            CredentialRepositoryPort credentialRepository,
            PasswordHasherPort passwordHasher,
            TokenHandlerPort tokenHandler
    ) {
        this.transactionalPort = transactionalPort;
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordHasher = passwordHasher;
        this.tokenHandler = tokenHandler;
    }

    public TokenView execute(LocalLoginCommand cmd) {
        return transactionalPort.executeReadOnly(() -> {
            Email email = new Email(cmd.email());
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.CREDENTIAL_INVALID));
            Credential credential = credentialRepository.findPasswordByUserId(user.getUserId())
                    .orElseThrow(() -> new ErpAuthException(AuthErrorCode.CREDENTIAL_INVALID));
            if (credential instanceof PasswordCredential pc) {
                if (!pc.verify(cmd.password(), passwordHasher)) {
                    throw new ErpAuthException(AuthErrorCode.CREDENTIAL_INVALID);
                }
                pc.recordUsage();
                credentialRepository.save(pc);
            } else {
                throw new ErpAuthException(AuthErrorCode.CREDENTIAL_INVALID);
            }

            TokenPair tokens = tokenHandler.generateTokens(user);
            return new TokenView(
                    tokens.accessToken(),
                    "Bearer",
                    tokens.accessTokenExpiresIn(),
                    tokens.refreshToken(),
                    "read write"
            );
        });
    }
}

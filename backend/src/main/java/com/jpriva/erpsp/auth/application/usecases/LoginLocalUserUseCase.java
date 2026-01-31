package com.jpriva.erpsp.auth.application.usecases;

import com.jpriva.erpsp.auth.application.dto.LocalLoginCommand;
import com.jpriva.erpsp.auth.application.dto.TokenView;
import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthException;
import com.jpriva.erpsp.auth.domain.model.credential.Credential;
import com.jpriva.erpsp.auth.domain.model.credential.PasswordCredential;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.ports.out.CredentialRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.auth.domain.ports.out.TransactionalPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.shared.domain.model.Email;

public class LoginLocalUserUseCase {

    private final TransactionalPort transactionalPort;
    private final UserRepositoryPort userRepository;
    private final CredentialRepositoryPort credentialRepository;
    private final PasswordHasherPort passwordHasher;

    public LoginLocalUserUseCase(
            TransactionalPort transactionalPort,
            UserRepositoryPort userRepository,
            CredentialRepositoryPort credentialRepository,
            PasswordHasherPort passwordHasher
    ) {
        this.transactionalPort = transactionalPort;
        this.userRepository = userRepository;
        this.credentialRepository = credentialRepository;
        this.passwordHasher = passwordHasher;
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
            TokenView token = new TokenView("", "", 0, "", "");
            return token;
        });
    }
}

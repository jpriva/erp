package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.shared.domain.model.token.verification.VerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepositoryPort {
    void save(VerificationToken token);

    Optional<VerificationToken> findByToken(UUID token);

    void deleteByToken(UUID token);

    void deleteByUserId(UUID userId);
}

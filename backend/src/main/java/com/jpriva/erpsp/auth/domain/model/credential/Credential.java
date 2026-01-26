package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.user.UserId;

public record Credential(
        CredentialId credentialId,
        UserId userId,
        AuthProvider provider,
        String identifier,
        Password secret
) {
}

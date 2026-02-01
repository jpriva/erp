package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.OpenIdTokenValidatorPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

/**
 * Credential type for OpenID Connect (OAuth2) authentication.
 */
public final class OpenIdCredential extends Credential {

    private final OpenIdProvider provider;
    private final OpenIdSubject subject;

    private OpenIdCredential(
            CredentialId credentialId,
            UserId userId,
            OpenIdProvider provider,
            OpenIdSubject subject,
            CredentialStatus status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        super(credentialId, userId, CredentialType.OPENID, status, createdAt, lastUsedAt);
        this.provider = provider;
        this.subject = subject;
    }

    /**
     * Creates a new OpenID credential for a user.
     *
     * @param userId   the user ID
     * @param provider the OpenID provider
     * @param subject  the subject identifier from the provider
     * @return a new active OpenID credential
     */
    public static OpenIdCredential create(UserId userId, OpenIdProvider provider, String subject) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(CredentialValidationError.USER_ID_EMPTY);
        }
        if (provider == null) {
            val.addError(CredentialValidationError.OPEN_ID_PROVIDER_EMPTY);
        }
        OpenIdSubject openIdSubject = null;
        try {
            openIdSubject = new OpenIdSubject(subject);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        return new OpenIdCredential(
                CredentialId.generate(),
                userId,
                provider,
                openIdSubject,
                CredentialStatus.ACTIVE,
                Instant.now(),
                null
        );
    }

    /**
     * Reconstructs an OpenID credential from persistence.
     *
     * @param credentialId the credential ID
     * @param userId       the user ID
     * @param provider     the OpenID provider name
     * @param subject      the subject identifier
     * @param status       the credential status
     * @param createdAt    the creation timestamp
     * @param lastUsedAt   the last usage timestamp
     * @return a reconstructed OpenID credential
     */
    public static OpenIdCredential fromPersistence(
            UUID credentialId,
            UUID userId,
            String provider,
            String subject,
            String status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        try {
            return new OpenIdCredential(
                    new CredentialId(credentialId),
                    new UserId(userId),
                    OpenIdProvider.of(provider),
                    new OpenIdSubject(subject),
                    CredentialStatus.of(status),
                    createdAt,
                    lastUsedAt
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    /**
     * Verifies if the provided ID token is valid for this credential.
     *
     * @param idToken   the ID token to verify
     * @param validator the token validator port
     * @return true if the token is valid and matches this credential, false otherwise
     */
    public boolean verify(String idToken, OpenIdTokenValidatorPort validator) {
        if (!isActive()) {
            return false;
        }
        return validator.validate(idToken, provider, subject.value());
    }

    public OpenIdProvider getProvider() {
        return provider;
    }

    public OpenIdSubject getSubject() {
        return subject;
    }
}

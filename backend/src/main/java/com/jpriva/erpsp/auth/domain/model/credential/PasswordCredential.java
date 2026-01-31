package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

/**
 * Credential type for local password-based authentication.
 */
public final class PasswordCredential extends Credential {

    private Password password;

    private PasswordCredential(
            CredentialId credentialId,
            UserId userId,
            Password password,
            CredentialStatus status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        super(credentialId, userId, CredentialType.PASSWORD, status, createdAt, lastUsedAt);
        this.password = password;
    }

    /**
     * Creates a new password credential for a user.
     *
     * @param userId      the user ID
     * @param rawPassword the plain text password
     * @param hasher      the password hasher
     * @return a new active password credential
     */
    public static PasswordCredential create(UserId userId, String rawPassword, PasswordHasherPort hasher) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(CredentialValidationError.USER_ID_EMPTY);
        }
        Password password = null;
        try {
            password = Password.create(rawPassword, hasher);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        return new PasswordCredential(
                CredentialId.generate(),
                userId,
                password,
                CredentialStatus.ACTIVE,
                Instant.now(),
                null
        );
    }

    /**
     * Reconstructs a password credential from persistence.
     *
     * @param credentialId the credential ID
     * @param userId       the user ID
     * @param passwordHash the persisted password hash
     * @param status       the credential status
     * @param createdAt    the creation timestamp
     * @param lastUsedAt   the last usage timestamp
     * @return a reconstructed password credential
     */
    public static PasswordCredential fromPersistence(
            UUID credentialId,
            UUID userId,
            String passwordHash,
            String status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        try {
            return new PasswordCredential(
                    new CredentialId(credentialId),
                    new UserId(userId),
                    Password.fromPersistence(passwordHash),
                    CredentialStatus.of(status),
                    createdAt,
                    lastUsedAt
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    /**
     * Verifies if the provided raw password matches the stored hash.
     *
     * @param rawPassword the plain text password to verify
     * @param hasher      the password hasher
     * @return true if the password matches, false otherwise
     */
    public boolean verify(String rawPassword, PasswordHasherPort hasher) {
        if (!isActive()) {
            return false;
        }
        return password.matches(rawPassword, hasher);
    }

    /**
     * Changes the password to a new one.
     *
     * @param newRawPassword the new plain text password
     * @param hasher         the password hasher
     */
    public void changePassword(String newRawPassword, PasswordHasherPort hasher) {
        this.password = Password.create(newRawPassword, hasher);
    }

    public Password getPassword() {
        return password;
    }
}

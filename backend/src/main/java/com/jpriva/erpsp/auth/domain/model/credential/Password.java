package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Value object representing a hashed password.
 * The raw password is never stored; only the hash is persisted.
 */
public record Password(String hash) {
    private static final String FIELD_NAME = "password";
    private static final String HASH_EMPTY_ERROR = "Password hash can't be empty";
    private static final String RAW_EMPTY_ERROR = "Password can't be empty";
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final String LENGTH_ERROR = "Password must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters";

    public Password {
        var val = ValidationError.builder();
        if (hash == null || hash.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, HASH_EMPTY_ERROR).build()
            );
        }
    }

    /**
     * Creates a new Password by hashing the raw password.
     *
     * @param rawPassword the plain text password
     * @param hasher      the password hasher port
     * @return a new Password with the hashed value
     */
    public static Password create(String rawPassword, PasswordHasherPort hasher) {
        var val = ValidationError.builder();
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, RAW_EMPTY_ERROR).build()
            );
        }
        if (rawPassword.length() < MIN_LENGTH || rawPassword.length() > MAX_LENGTH) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, LENGTH_ERROR).build()
            );
        }
        return new Password(hasher.encode(rawPassword));
    }

    /**
     * Creates a Password from a persisted hash.
     * No validation of the raw password is performed.
     *
     * @param hash the persisted hash
     * @return a new Password with the given hash
     */
    public static Password fromPersistence(String hash) {
        return new Password(hash);
    }

    /**
     * Verifies if the raw password matches this password's hash.
     *
     * @param rawPassword the plain text password to verify
     * @param hasher      the password hasher port
     * @return true if the password matches, false otherwise
     */
    public boolean matches(String rawPassword, PasswordHasherPort hasher) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        return hasher.matches(rawPassword, hash);
    }
}

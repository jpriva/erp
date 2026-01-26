package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record CredentialId(UUID value) {
    private static final String FIELD_NAME = "credentialId";
    private static final String EMPTY_VALUE = "Can't create an empty credential id.";
    private static final String INVALID_FORMAT = "Invalid credential id format.";

    public CredentialId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
    }

    public static CredentialId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
        try {
            return new CredentialId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, INVALID_FORMAT).build(),
                    e
            );
        }
    }

    public static CredentialId generate() {
        return new CredentialId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

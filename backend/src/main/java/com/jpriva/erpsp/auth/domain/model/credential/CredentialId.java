package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record CredentialId(UUID value) {

    public CredentialId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.ID_EMPTY).build()
            );
        }
    }

    public static CredentialId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new CredentialId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.ID_INVALID_FORMAT).build(), e
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

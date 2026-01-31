package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Value object representing the subject identifier from an OpenID provider.
 * This is the unique identifier for the user within that specific provider.
 */
public record OpenIdSubject(String value) {
    private static final int MAX_LENGTH = 255;

    public OpenIdSubject {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.OPEN_ID_SUBJECT_EMPTY).build()
            );
        }
        if (value.length() > MAX_LENGTH) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.OPEN_ID_SUBJECT_LENGTH_INVALID).build()
            );
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}

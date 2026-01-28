package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Value object representing a biometric template identifier.
 * This is a reference to the stored biometric template in the verification system.
 */
public record BiometricTemplateId(String value) {
    private static final String FIELD_NAME = "biometricTemplateId";
    private static final String EMPTY_VALUE = "Biometric template ID can't be empty";
    private static final int MAX_LENGTH = 255;
    private static final String LENGTH_ERROR = "Biometric template ID exceeds maximum length of " + MAX_LENGTH + " characters";

    public BiometricTemplateId {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
        if (value.length() > MAX_LENGTH) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, LENGTH_ERROR).build()
            );
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}

package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum BiometricType {
    FINGERPRINT,
    FACE,
    IRIS,
    VOICE;

    private static final String TYPE_NULL_ERROR = "Biometric type can't be empty";
    private static final String TYPE_NOT_FOUND_ERROR = "Biometric type doesn't exist";
    private static final String FIELD_TYPE = "biometricType";

    public static BiometricType of(String type) {
        var val = new ValidationError.Builder();
        if (type == null || type.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_TYPE, TYPE_NULL_ERROR).build()
            );
        }
        BiometricType biometricType;
        try {
            biometricType = BiometricType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_TYPE, TYPE_NOT_FOUND_ERROR).build()
            );
        }
        return biometricType;
    }
}

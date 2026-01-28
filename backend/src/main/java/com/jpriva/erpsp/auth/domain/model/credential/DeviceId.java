package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Value object representing a device identifier.
 * This links the biometric credential to a specific device.
 */
public record DeviceId(String value) {
    private static final String FIELD_NAME = "deviceId";
    private static final String EMPTY_VALUE = "Device ID can't be empty";
    private static final int MAX_LENGTH = 255;
    private static final String LENGTH_ERROR = "Device ID exceeds maximum length of " + MAX_LENGTH + " characters";

    public DeviceId {
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

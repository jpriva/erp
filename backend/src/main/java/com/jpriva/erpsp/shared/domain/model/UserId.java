package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.constants.AuthValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.UUID;

public record UserId(UUID value) {

    public UserId {
        var val = ValidationError.builder();
        if (value == null) {
            val.addError(AuthValidationError.USER_ID_EMPTY);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
    }

    public static UserId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            val.addError(AuthValidationError.USER_ID_EMPTY);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
        try {
            assert value != null;
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            val.addError(AuthValidationError.USER_ID_INVALID_FORMAT);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
        return null;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

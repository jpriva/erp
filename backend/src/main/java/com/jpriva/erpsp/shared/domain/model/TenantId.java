package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.constants.AuthValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.UUID;

public record TenantId(UUID value) {

    public TenantId {
        var val = ValidationError.builder();
        if (value == null) {
            val.addError(AuthValidationError.TENANT_ID_EMPTY);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
    }

    public static TenantId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            val.addError(AuthValidationError.TENANT_ID_EMPTY);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
        try {
            assert value != null;
            return new TenantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            val.addError(AuthValidationError.TENANT_ID_INVALID_FORMAT);
            ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
        }
        return null;
    }

    public static TenantId generate() {
        return new TenantId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

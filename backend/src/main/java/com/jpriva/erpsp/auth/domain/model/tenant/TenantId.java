package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record TenantId(UUID value) {
    private static final String FIELD_NAME = "tenantId";
    private static final String EMPTY_VALUE = "Can't create an empty tenant id.";
    private static final String INVALID_FORMAT = "Invalid tenant id format.";

    public TenantId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
    }

    public static TenantId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
        try {
            return new TenantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, INVALID_FORMAT).build(),
                    e
            );
        }
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

package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.TenantValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record TenantId(UUID value) {

    public TenantId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpAuthValidationException(
                    val.addError(TenantValidationError.ID_EMPTY).build()
            );
        }
    }

    public static TenantId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(TenantValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new TenantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(TenantValidationError.ID_INVALID_FORMAT).build(), e
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

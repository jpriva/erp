package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.constants.RoleValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record RoleId(UUID value) {

    public RoleId {
        if (value == null) {
            throw new ErpAuthValidationException(
                    ValidationError.createSingle(RoleValidationError.ID_EMPTY)
            );
        }
    }

    public static RoleId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(RoleValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new RoleId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(RoleValidationError.ID_INVALID_FORMAT).build(), e
            );
        }
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

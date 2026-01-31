package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.auth.domain.constants.UserValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record UserId(UUID value) {
    private static final String FIELD_NAME = "userId";
    private static final String EMPTY_VALUE = "validation.user.id.empty";
    private static final String INVALID_FORMAT = "validation.user.id.invalid.format";

    public UserId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpAuthValidationException(
                    val.addError(UserValidationError.ID_EMPTY).build()
            );
        }
    }

    public static UserId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(UserValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(UserValidationError.ID_EMPTY).build(), e
            );
        }
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

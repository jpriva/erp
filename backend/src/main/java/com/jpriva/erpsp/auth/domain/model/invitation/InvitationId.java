package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.InvitationValidationError;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record InvitationId(UUID value) {

    public InvitationId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(InvitationValidationError.ID_EMPTY).build()
            );
        }
    }

    public static InvitationId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(InvitationValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new InvitationId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(InvitationValidationError.ID_INVALID_FORMAT).build(),
                    e
            );
        }
    }

    public static InvitationId generate() {
        return new InvitationId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

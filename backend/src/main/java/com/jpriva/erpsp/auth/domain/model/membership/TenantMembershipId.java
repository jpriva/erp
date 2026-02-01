package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.TenantMembershipValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.UUID;

public record TenantMembershipId(UUID value) {

    public TenantMembershipId {
        var val = ValidationError.builder();
        if (value == null) {
            throw new ErpAuthValidationException(
                    val.addError(TenantMembershipValidationError.ID_EMPTY).build()
            );
        }
    }

    public static TenantMembershipId from(String value) {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(TenantMembershipValidationError.ID_EMPTY).build()
            );
        }
        try {
            return new TenantMembershipId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(TenantMembershipValidationError.ID_INVALID_FORMAT).build(),
                    e
            );
        }
    }

    public static TenantMembershipId generate() {
        return new TenantMembershipId(UUID.randomUUID());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value.toString();
    }
}

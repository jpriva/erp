package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.UUID;

public record RoleId(UUID value) {
    public RoleId {
        if (value == null) {
            var val = new ValidationError.Builder();
            val.addError("roleId", "Role ID cannot be null.");
            throw new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
        }
    }

    public static RoleId generate() {
        return new RoleId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

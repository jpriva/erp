package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.RoleValidationError;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record RoleName(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;

    public RoleName {
        var val = ValidationError.builder();
        if (value == null) value = "";
        value = value.trim();
        if (value.length() < MIN_LENGTH) {
            val.addError(RoleValidationError.NAME_MIN_LENGTH);
        }
        if (value.length() > MAX_LENGTH) {
            val.addError(RoleValidationError.NAME_MAX_LENGTH);
        }

        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}

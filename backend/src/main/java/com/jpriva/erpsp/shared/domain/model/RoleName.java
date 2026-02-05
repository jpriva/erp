package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.constants.AuthValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record RoleName(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;

    public RoleName {
        var val = ValidationError.builder();
        if (value == null) value = "";
        value = value.trim();
        if (value.length() < MIN_LENGTH) {
            val.addError(AuthValidationError.NAME_MIN_LENGTH);
        }
        if (value.length() > MAX_LENGTH) {
            val.addError(AuthValidationError.NAME_MAX_LENGTH);
        }

        ValidationErrorUtils.validate(AuthValidationError.AUTH_MODULE, val);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}

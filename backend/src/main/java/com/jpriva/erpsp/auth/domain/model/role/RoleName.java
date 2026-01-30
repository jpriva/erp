package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record RoleName(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;
    private static final String FIELD_NAME = "roleName";

    public RoleName {
        var val = ValidationError.builder();
        if (value == null) value = "";
        value = value.trim();
        if (value.length() < MIN_LENGTH) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorGreaterOrEqualThan("Role name", MIN_LENGTH, "characters")
            );
        }
        if (value.length() > MAX_LENGTH) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorLessOrEqualThan("Role name", MAX_LENGTH, "characters")
            );
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
    }
}

package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record TenantName(String value) {
    private static final String FIELD_NAME = "tenantName";
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public TenantName {
        var val = ValidationError.builder();
        if (value == null) value = "";
        value = value.trim();
        if (value.length() < MIN_LENGTH) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorGreaterOrEqualThan("Tenant name", MIN_LENGTH, "characters")
            );
        }
        if (value.length() > MAX_LENGTH) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorLessOrEqualThan("Tenant name", MAX_LENGTH, "characters")
            );
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}

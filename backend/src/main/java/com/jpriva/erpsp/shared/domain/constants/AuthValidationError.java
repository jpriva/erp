package com.jpriva.erpsp.shared.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum AuthValidationError implements ValidationErrorCode {

    USER_ID_EMPTY("userId", "validation.auth.user.id.empty", "User ID cannot be empty."),
    USER_ID_INVALID_FORMAT("userId", "validation.auth.user.id.invalid.format", "Invalid user ID format."),
    TENANT_ID_EMPTY("tenantId", "validation.auth.tenant.id.empty", "Tenant ID cannot be empty."),
    TENANT_ID_INVALID_FORMAT("tenantId", "validation.auth.tenant.id.invalid.format", "Tenant ID format is invalid."),
    NAME_EMPTY("roleName", "validation.auth.role.name.empty", "Role name cannot be empty."),
    NAME_MIN_LENGTH("roleName", "validation.auth.role.name.min.length", "Role name must be at least 3 characters long."),
    NAME_MAX_LENGTH("roleName", "validation.auth.role.name.max.length", "Role name cannot exceed 50 characters.");

    public static final String AUTH_MODULE = "AUTH";

    private final String code;
    private final String message;
    private final String field;

    AuthValidationError(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

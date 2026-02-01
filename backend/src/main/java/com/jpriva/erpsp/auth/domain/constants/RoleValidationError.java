package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum RoleValidationError implements ValidationErrorCode {
    ID_EMPTY("roleId", "validation.auth.role.id.empty", "Role ID cannot be empty."),
    ID_INVALID_FORMAT("roleId", "validation.auth.role.id.invalid.format", "Role ID format is invalid."),
    TENANT_ID_EMPTY("tenantId", "validation.auth.role.tenant.id.empty", "Tenant ID cannot be empty."),
    NAME_EMPTY("roleName", "validation.auth.role.name.empty", "Role name cannot be empty."),
    NAME_MIN_LENGTH("roleName", "validation.auth.role.name.min.length", "Role name must be at least 3 characters long."),
    NAME_MAX_LENGTH("roleName", "validation.auth.role.name.max.length", "Role name cannot exceed 50 characters.");


    private final String code;
    private final String message;
    private final String field;

    RoleValidationError(String field, String code, String message) {
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
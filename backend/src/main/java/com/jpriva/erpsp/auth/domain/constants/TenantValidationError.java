package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum TenantValidationError implements ValidationErrorCode {
    ID_EMPTY("tenantId", "validation.auth.tenant.id.empty", "Tenant ID cannot be empty."),
    ID_INVALID_FORMAT("tenantId", "validation.auth.tenant.id.invalid.format", "Tenant ID format is invalid."),
    OWNER_ID_EMPTY("ownerId", "validation.auth.owner.id.empty", "Owner ID cannot be empty."),
    NAME_EMPTY("name", "validation.auth.tenant.name.empty", "Tenant name cannot be empty."),
    NAME_MIN_LENGTH("name", "validation.auth.tenant.name.min.length", "Tenant name must be at least 3 characters long."),
    NAME_MAX_LENGTH("name", "validation.auth.tenant.name.max.length", "Tenant name cannot exceed 50 characters."),
    STATUS_EMPTY("status", "validation.auth.tenant.status.empty", "Tenant status cannot be empty."),
    STATUS_NOT_FOUND("status", "validation.auth.tenant.status.not.found", "Tenant status not found."),
    CREATED_AT_EMPTY("createdAt", "validation.auth.tenant.createdAt.empty", "Tenant creation date cannot be empty.");

    private final String code;
    private final String message;
    private final String field;

    TenantValidationError(String field, String code, String message) {
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

package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum UserValidationError implements ValidationErrorCode {
    ID_EMPTY("userId", "validation.auth.user.id.empty", "User ID cannot be empty."),
    ID_INVALID_FORMAT("userId", "validation.auth.user.id.invalid.format", "Invalid user ID format."),
    EMAIL_EMPTY("email", "validation.auth.user.email.empty", "Email cannot be empty"),
    NAME_EMPTY("name", "validation.auth.user.name.empty", "Name cannot be empty"),
    STATUS_EMPTY("status", "validation.auth.user.status.empty", "Status cannot be empty"),
    STATUS_NOT_FOUND("status", "validation.auth.user.status.not.found", "Invalid user status"),
    CREATED_AT_EMPTY("createdAt", "validation.auth.user.createdAt.empty", "User creation date cannot be empty");

    private final String code;
    private final String message;
    private final String field;

    UserValidationError(String field, String code, String message) {
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
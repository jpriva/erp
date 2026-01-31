package com.jpriva.erpsp.shared.domain.exceptions;

public enum ErpValidationError implements ValidationErrorCode {
    EMAIL_EMPTY("email", "validation.shared.email.empty", "Email cannot be empty."),
    EMAIL_INVALID_FORMAT("email", "validation.shared.email.invalid", "Invalid email format."),
    EMAIL_MAX_LENGTH("email", "validation.shared.email.max.length", "Email cannot exceed 254 characters."),
    FIRST_NAME_EMPTY("firstName", "validation.shared.first.name.empty", "First name cannot be empty."),
    FIRST_NAME_MIN_LENGTH("firstName", "validation.shared.first.name.min.length", "First name must be at least 2 characters long."),
    FIRST_NAME_MAX_LENGTH("firstName", "validation.shared.first.name.max.length", "First name cannot exceed 100 characters."),
    LAST_NAME_EMPTY("lastName", "validation.shared.last.name.empty", "Last name cannot be empty."),
    LAST_NAME_MIN_LENGTH("lastName", "validation.shared.last.name.min.length", "Last name must be at least 2 characters long."),
    LAST_NAME_MAX_LENGTH("lastName", "validation.shared.last.name.max.length", "Last name cannot exceed 100 characters.");

    private final String code;
    private final String message;
    private final String field;

    ErpValidationError(String field, String code, String message) {
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
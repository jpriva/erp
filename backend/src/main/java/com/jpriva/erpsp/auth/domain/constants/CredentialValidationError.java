package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum CredentialValidationError implements ValidationErrorCode {
    ID_EMPTY("credentialId", "validation.auth.credential.id.empty", "Credential ID cannot be empty."),
    ID_INVALID_FORMAT("credentialId", "validation.auth.credential.id.invalid.format", "Credential ID format is invalid."),
    USER_ID_EMPTY("userId", "validation.auth.credential.userId.empty", "User ID cannot be empty."),
    TYPE_EMPTY("type", "validation.auth.credential.type.empty", "Credential type cannot be empty."),
    TYPE_INVALID("type", "validation.auth.credential.type.invalid", "Credential type is invalid."),
    STATUS_EMPTY("status", "validation.auth.credential.status.empty", "Credential status cannot be empty."),
    STATUS_NOT_FOUND("status", "validation.auth.credential.status.not.found", "Credential status doesn't exist"),
    STATUS_INVALID("status", "validation.auth.credential.status.invalid", "Credential status is invalid."),
    CREATED_AT_EMPTY("createdAt", "validation.auth.credential.createdAt", "Creation timestamp cannot be empty"),
    LAST_USED_AT_EMPTY("lastUsedAt", "validation.auth.credential.lastUsedAt", "Last usage timestamp cannot be empty"),
    PASSWORD_EMPTY("password", "validation.auth.credential.password.empty", "Password cannot be empty."),
    PASSWORD_LENGTH_INVALID("password", "validation.auth.credential.password.length.invalid", "Password must be between 8 and 40 characters"),
    OPEN_ID_SUBJECT_EMPTY("openIdSubject", "validation.auth.credential.openIdSubject.empty", "OpenID subject cannot be empty."),
    OPEN_ID_SUBJECT_LENGTH_INVALID("openIdSubject", "validation.auth.credential.openIdSubject.length.invalid", "OpenID subject must be between 1 and 255 characters"),
    OPEN_ID_PROVIDER_EMPTY("openIdProvider", "validation.auth.credential.openIdProvider.empty", "OpenID provider cannot be empty."),
    OPEN_ID_PROVIDER_NOT_FOUND("openIdProvider", "validation.auth.credential.openIdProvider.not.found", "OpenID provider doesn't exist"),
    DEVICE_ID_EMPTY("deviceId", "validation.auth.credential.deviceId.empty", "Device ID cannot be empty."),
    DEVICE_ID_INVALID_LENGTH("deviceId", "validation.auth.credential.deviceId.invalid.length", "Device ID must be between 1 and 255 characters");


    private final String code;
    private final String message;
    private final String field;

    CredentialValidationError(String field, String code, String message) {
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
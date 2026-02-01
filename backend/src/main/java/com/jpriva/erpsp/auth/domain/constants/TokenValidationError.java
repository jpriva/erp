package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum TokenValidationError implements ValidationErrorCode {
    TOKEN_EXPIRED("token", "validation.auth.token.expired", "Token has expired"),
    TOKEN_INVALID("token", "validation.auth.token.invalid", "Token is invalid"),
    TOKEN_MALFORMED("token", "validation.auth.token.malformed", "Token is malformed"),
    REFRESH_TOKEN_INVALID("refreshToken", "validation.auth.token.refresh.invalid", "Refresh token is invalid"),
    TOKEN_TYPE_MISMATCH("token", "validation.auth.token.type.mismatch", "Token type does not match expected");

    private final String field;
    private final String code;
    private final String message;

    TokenValidationError(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

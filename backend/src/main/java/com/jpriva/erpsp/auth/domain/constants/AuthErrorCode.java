package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", 404);

    public static final String AUTH_MODULE = "AUTH";

    private final String code;
    private final String message;
    private final int status;

    AuthErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatus() {
        return status;
    }
}

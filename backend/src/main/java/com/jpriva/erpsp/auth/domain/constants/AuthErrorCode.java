package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", 404),
    CREDENTIAL_NOT_FOUND("CREDENTIAL_NOT_FOUND", "Credential not found.", 404),
    CREDENTIAL_INVALID("CREDENTIAL_INVALID", "Invalid credential.", 401),
    CREDENTIAL_EXPIRED("CREDENTIAL_EXPIRED", "Credential has expired.", 401),
    CREDENTIAL_DISABLED("CREDENTIAL_DISABLED", "Credential is disabled.", 401),
    CREDENTIAL_COMPROMISED("CREDENTIAL_COMPROMISED", "Credential has been compromised.", 401),
    PASSWORD_VERIFICATION_FAILED("PASSWORD_VERIFICATION_FAILED", "Password verification failed.", 401),
    OPENID_TOKEN_INVALID("OPENID_TOKEN_INVALID", "Invalid OpenID token.", 401),
    BIOMETRIC_VERIFICATION_FAILED("BIOMETRIC_VERIFICATION_FAILED", "Biometric verification failed.", 401),
    DEVICE_MISMATCH("DEVICE_MISMATCH", "Device mismatch.", 401),
    TENANT_NOT_FOUND("TENANT_NOT_FOUND", "Tenant not found.", 404),
    TENANT_SUSPENDED("TENANT_SUSPENDED", "Tenant is suspended.", 403),
    TENANT_DELETED("TENANT_DELETED", "Tenant has been deleted.", 410);

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

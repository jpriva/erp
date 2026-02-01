package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ErrorCode;

public enum AuthErrorCode implements ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found.", 404),
    USER_ALREADY_EXISTS("USER_ALREADY_EXISTS", "User already exists.", 409),
    USER_ALREADY_MEMBER("USER_ALREADY_MEMBER", "User is already a member of the tenant.", 409),
    USER_ALREADY_HAS_ROLE("USER_ALREADY_HAS_ROLE", "User already has the specified role.", 409),
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
    TENANT_DELETED("TENANT_DELETED", "Tenant has been deleted.", 410),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role not found.", 404),
    ROLE_NOT_ASSIGNED("ROLE_NOT_ASSIGNED", "Role is not assigned to the member.", 404),
    NOT_AN_OWNER("NOT_AN_OWNER", "User is not the owner of the tenant.", 403),
    OWNER_MUST_TRANSFER_OWNERSHIP("OWNER_MUST_TRANSFER_OWNERSHIP", "User must transfer ownership before performing this action.", 403),
    MEMBERSHIP_NOT_FOUND("MEMBERSHIP_NOT_FOUND", "Membership not found.", 404),
    MEMBERSHIP_ALREADY_EXISTS("MEMBERSHIP_ALREADY_EXISTS", "Membership already exists.", 409),
    INVITATION_NOT_FOUND("INVITATION_NOT_FOUND", "Invitation not found.", 404),
    INVITATION_NOT_VALID("INVITATION_NOT_VALID", "Invitation is not valid.", 400),
    INVITATION_EXPIRED("INVITATION_EXPIRED", "Invitation has expired.", 400),
    INVITATION_EMAIL_MISMATCH("INVITATION_EMAIL_MISMATCH", "Invitation email does not match the user's email.", 400),
    INVITATION_ALREADY_EXISTS("INVITATION_ALREADY_EXISTS", "An invitation already exists for the same email and tenant.", 400),
    TOKEN_INVALID("TOKEN_INVALID", "Token is invalid or expired.", 401),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "Token has expired.", 401);

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

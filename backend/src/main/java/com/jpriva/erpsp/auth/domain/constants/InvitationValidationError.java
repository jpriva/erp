package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum InvitationValidationError implements ValidationErrorCode {
    ID_EMPTY("invitationId", "validation.auth.invitation.id.empty", "Invitation ID cannot be empty."),
    ID_INVALID_FORMAT("invitationId", "validation.auth.invitation.id.invalid.format", "Invitation ID format is invalid."),
    TENANT_ID_EMPTY("tenantId", "validation.auth.invitation.tenant.id.empty", "Tenant ID cannot be empty."),
    TOKEN_EMPTY("token", "validation.auth.invitation.token.empty", "Invitation token cannot be empty."),
    TOKEN_FORMAT("token", "validation.auth.invitation.token.format", "Invitation token format is invalid."),
    STATUS_EMPTY("status", "validation.auth.invitation.status.empty", "Invitation status cannot be empty."),
    STATUS_NOT_FOUND("status", "validation.auth.invitation.status.not.found", "Invitation status doesn't exist"),
    STATUS_INVALID("status", "validation.auth.invitation.status.invalid", "Invitation status is invalid."),
    STATUS_EXPIRED("status", "validation.auth.invitation.status.expired", "Invitation has expired."),
    EMAIL_EMPTY("email", "validation.auth.invitation.email.empty", "Email cannot be empty."),
    INVITED_BY_EMPTY("invitedBy", "validation.auth.invitation.invitedBy.empty", "Invited by cannot be empty."),
    ROLE_IDS_EMPTY("roleIds", "validation.auth.invitation.roleIds.empty", "Role IDs cannot be empty."),
    VALID_FOR_INVALID("validFor", "validation.auth.invitation.validFor.invalid", "Validity duration must be positive"),
    CREATED_AT_EMPTY("createdAt", "validation.auth.invitation.createdAt", "Creation timestamp cannot be empty"),
    EXPIRES_AT_EMPTY("expiresAt", "validation.auth.invitation.expiresAt", "Expiration timestamp cannot be empty"),
    USER_ID_EMPTY("userId", "validation.auth.invitation.userId", "User ID cannot be empty");


    private final String code;
    private final String message;
    private final String field;

    InvitationValidationError(String field, String code, String message) {
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
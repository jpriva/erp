package com.jpriva.erpsp.auth.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum TenantMembershipValidationError implements ValidationErrorCode {
    ID_EMPTY("tenantMembershipId", "validation.auth.tenantMembership.id.empty", "Tenant membership ID cannot be empty."),
    ID_INVALID_FORMAT("tenantMembershipId", "validation.auth.tenantMembership.id.invalid.format", "Tenant membership ID format is invalid."),
    USER_ID_EMPTY("userId", "validation.auth.tenantMembership.userId.empty", "User ID cannot be empty."),
    TENANT_ID_EMPTY("tenantId", "validation.auth.tenantMembership.tenantId.empty", "Tenant ID cannot be empty."),
    STATUS_EMPTY("status", "validation.auth.tenantMembership.status.empty", "Membership status cannot be empty."),
    STATUS_NOT_FOUND("status", "validation.auth.tenantMembership.status.notFound", "Membership status not found."),
    JOINED_AT_EMPTY("joinedAt", "validation.auth.tenantMembership.joinedAt.empty", "Joined at cannot be empty."),
    INVITED_BY_EMPTY("invitedBy", "validation.auth.tenantMembership.invitedBy.empty", "Invited by user ID cannot be empty."),
    ROLES_EMPTY("roles", "validation.auth.tenantMembership.roles.empty", "Roles cannot be empty for active membership."),
    ROLES_EMPTY_FOR_ACTIVE("roles", "validation.auth.tenantMembership.roles.emptyForActive", "Roles cannot be empty for active membership."),
    ROLE_ID_EMPTY("roleId", "validation.auth.tenantMembership.role.id.empty", "Role ID cannot be empty."),
    ROLE_NAME_EMPTY("roleName", "validation.auth.tenantMembership.role.name.empty", "Role name cannot be empty"),
    ASSIGNED_AT_EMPTY("assignedAt", "validation.auth.tenantMembership.role.assignedAt.empty", "Assigned at cannot be empty"),
    ASSIGNED_BY_EMPTY("assignedBy", "validation.auth.tenantMembership.role.assignedBy.empty", "Assigned by cannot be empty"),
    ROLE_ALREADY_ASSIGNED("roles", "validation.auth.tenantMembership.role.alreadyAssigned", "Role is already assigned to this membership"),
    ROLE_NOT_ASSIGNED("roles", "validation.auth.tenantMembership.role.notAssigned", "Role is not assigned to this membership"),
    CANNOT_REVOKE_LAST_ROLE("roles", "validation.auth.tenantMembership.role.cannotRevokeLastRole", "Cannot revoke the last role of an active membership");

    private final String code;
    private final String message;
    private final String field;

    TenantMembershipValidationError(String field, String code, String message) {
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

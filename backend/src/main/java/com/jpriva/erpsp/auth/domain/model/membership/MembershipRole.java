package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.TenantMembershipValidationError;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.shared.domain.model.RoleName;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.time.Instant;

/**
 * Denormalized snapshot of a role assignment within a tenant membership.
 * Contains role metadata and audit information about the assignment.
 */
public record MembershipRole(
        RoleId roleId,
        RoleName roleName,
        Instant assignedAt,
        UserId assignedBy
) {

    public MembershipRole {
        var val = ValidationError.builder();
        if (roleId == null) {
            val.addError(TenantMembershipValidationError.ROLE_ID_EMPTY);
        }
        if (roleName == null) {
            val.addError(TenantMembershipValidationError.ROLE_NAME_EMPTY);
        }
        if (assignedAt == null) {
            val.addError(TenantMembershipValidationError.ASSIGNED_AT_EMPTY);
        }
        if (assignedBy == null) {
            val.addError(TenantMembershipValidationError.ASSIGNED_BY_EMPTY);
        }
        if (val.hasErrors()) {
            throw new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
        }
    }

    public static MembershipRole create(RoleId roleId, RoleName roleName, UserId assignedBy) {
        return new MembershipRole(roleId, roleName, Instant.now(), assignedBy);
    }

    public static MembershipRole fromPersistence(
            java.util.UUID roleId,
            String roleName,
            Instant assignedAt,
            java.util.UUID assignedBy
    ) {
        return new MembershipRole(
                new RoleId(roleId),
                new RoleName(roleName),
                assignedAt,
                new UserId(assignedBy)
        );
    }
}

package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
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
    private static final String ROLE_ID_NULL_ERROR = "Role ID cannot be null";
    private static final String ROLE_NAME_NULL_ERROR = "Role name cannot be null";
    private static final String ASSIGNED_AT_NULL_ERROR = "Assigned at cannot be null";
    private static final String ASSIGNED_BY_NULL_ERROR = "Assigned by cannot be null";

    public MembershipRole {
        var val = ValidationError.builder();
        if (roleId == null) {
            val.addError("roleId", ROLE_ID_NULL_ERROR);
        }
        if (roleName == null) {
            val.addError("roleName", ROLE_NAME_NULL_ERROR);
        }
        if (assignedAt == null) {
            val.addError("assignedAt", ASSIGNED_AT_NULL_ERROR);
        }
        if (assignedBy == null) {
            val.addError("assignedBy", ASSIGNED_BY_NULL_ERROR);
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

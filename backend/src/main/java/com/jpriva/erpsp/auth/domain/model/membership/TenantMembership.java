package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root representing a user's membership in a tenant with their assigned roles.
 *
 * Invariants:
 * - Active membership must have at least one role
 * - No duplicate roles by RoleId
 * - User can only have one membership per tenant
 * - Cannot revoke the last role of an active membership (must remove membership instead)
 */
public class TenantMembership {
    private static final String MEMBERSHIP_ID_NULL_ERROR = "Membership ID can't be empty";
    private static final String USER_ID_NULL_ERROR = "User ID can't be empty";
    private static final String TENANT_ID_NULL_ERROR = "Tenant ID can't be empty";
    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String JOINED_AT_NULL_ERROR = "Joined at can't be empty";
    private static final String INVITED_BY_NULL_ERROR = "Invited by can't be empty";
    private static final String ROLES_NULL_ERROR = "Roles can't be null";
    private static final String ROLES_EMPTY_FOR_ACTIVE_ERROR = "Active membership must have at least one role";
    private static final String ROLE_ALREADY_ASSIGNED_ERROR = "Role is already assigned to this membership";
    private static final String ROLE_NOT_ASSIGNED_ERROR = "Role is not assigned to this membership";
    private static final String CANNOT_REVOKE_LAST_ROLE_ERROR = "Cannot revoke the last role of an active membership";

    private static final String FIELD_MEMBERSHIP_ID = "membershipId";
    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_TENANT_ID = "tenantId";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_JOINED_AT = "joinedAt";
    private static final String FIELD_INVITED_BY = "invitedBy";
    private static final String FIELD_ROLES = "roles";

    private final TenantMembershipId membershipId;
    private final UserId userId;
    private final TenantId tenantId;
    private MembershipStatus status;
    private final Instant joinedAt;
    private final UserId invitedBy;
    private final Set<MembershipRole> roles;

    public TenantMembership(
            TenantMembershipId membershipId,
            UserId userId,
            TenantId tenantId,
            MembershipStatus status,
            Instant joinedAt,
            UserId invitedBy,
            Set<MembershipRole> roles
    ) {
        var val = new ValidationError.Builder();
        if (membershipId == null) {
            val.addError(FIELD_MEMBERSHIP_ID, MEMBERSHIP_ID_NULL_ERROR);
        }
        if (userId == null) {
            val.addError(FIELD_USER_ID, USER_ID_NULL_ERROR);
        }
        if (tenantId == null) {
            val.addError(FIELD_TENANT_ID, TENANT_ID_NULL_ERROR);
        }
        if (status == null) {
            val.addError(FIELD_STATUS, STATUS_NULL_ERROR);
        }
        if (joinedAt == null) {
            val.addError(FIELD_JOINED_AT, JOINED_AT_NULL_ERROR);
        }
        if (invitedBy == null) {
            val.addError(FIELD_INVITED_BY, INVITED_BY_NULL_ERROR);
        }
        if (roles == null) {
            val.addError(FIELD_ROLES, ROLES_NULL_ERROR);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Invariant: active membership must have at least one role
        if (status == MembershipStatus.ACTIVE && roles.isEmpty()) {
            val.addError(FIELD_ROLES, ROLES_EMPTY_FOR_ACTIVE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        this.membershipId = membershipId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.status = status;
        this.joinedAt = joinedAt;
        this.invitedBy = invitedBy;
        this.roles = new HashSet<>(roles);
    }

    /**
     * Factory method to create a new membership with initial roles.
     */
    public static TenantMembership create(
            UserId userId,
            TenantId tenantId,
            Set<MembershipRole> initialRoles,
            UserId invitedBy
    ) {
        var val = new ValidationError.Builder();
        if (initialRoles == null || initialRoles.isEmpty()) {
            val.addError(FIELD_ROLES, ROLES_EMPTY_FOR_ACTIVE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        return new TenantMembership(
                TenantMembershipId.generate(),
                userId,
                tenantId,
                MembershipStatus.ACTIVE,
                Instant.now(),
                invitedBy,
                initialRoles
        );
    }

    /**
     * Factory method to restore membership from persistence layer.
     */
    public static TenantMembership fromPersistence(
            UUID membershipId,
            UUID userId,
            UUID tenantId,
            String status,
            Instant joinedAt,
            UUID invitedBy,
            Set<MembershipRole> roles
    ) {
        try {
            return new TenantMembership(
                    new TenantMembershipId(membershipId),
                    new UserId(userId),
                    new TenantId(tenantId),
                    MembershipStatus.of(status),
                    joinedAt,
                    new UserId(invitedBy),
                    roles
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    /**
     * Assigns a new role to this membership.
     *
     * @throws ErpValidationException if role is already assigned
     */
    public void assignRole(RoleId roleId, RoleName roleName, UserId assignedBy) {
        var val = new ValidationError.Builder();
        if (roleId == null) {
            val.addError("roleId", "Role ID cannot be null");
        }
        if (roleName == null) {
            val.addError("roleName", "Role name cannot be null");
        }
        if (assignedBy == null) {
            val.addError("assignedBy", "Assigned by cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Check if role is already assigned
        boolean roleExists = roles.stream()
                .anyMatch(mr -> mr.roleId().equals(roleId));
        if (roleExists) {
            val.addError(FIELD_ROLES, ROLE_ALREADY_ASSIGNED_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        roles.add(MembershipRole.create(roleId, roleName, assignedBy));
    }

    /**
     * Revokes a role from this membership.
     *
     * Invariant: Cannot revoke the last role of an active membership.
     *
     * @throws ErpValidationException if role is not assigned or would violate invariant
     */
    public void revokeRole(RoleId roleId) {
        var val = new ValidationError.Builder();
        if (roleId == null) {
            val.addError("roleId", "Role ID cannot be null");
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Check if role is assigned
        MembershipRole roleToRevoke = roles.stream()
                .filter(mr -> mr.roleId().equals(roleId))
                .findFirst()
                .orElse(null);

        if (roleToRevoke == null) {
            val.addError(FIELD_ROLES, ROLE_NOT_ASSIGNED_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Invariant: cannot revoke last role of active membership
        if (status == MembershipStatus.ACTIVE && roles.size() == 1) {
            val.addError(FIELD_ROLES, CANNOT_REVOKE_LAST_ROLE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        roles.remove(roleToRevoke);
    }

    /**
     * Checks if this membership has a specific role.
     */
    public boolean hasRole(RoleId roleId) {
        return roles.stream()
                .anyMatch(mr -> mr.roleId().equals(roleId));
    }

    /**
     * Checks if this membership has any of the provided roles.
     */
    public boolean hasAnyRole(Set<RoleId> roleIds) {
        return roles.stream()
                .map(MembershipRole::roleId)
                .anyMatch(roleIds::contains);
    }

    /**
     * Checks if this membership has all of the provided roles.
     */
    public boolean hasAllRoles(Set<RoleId> roleIds) {
        Set<RoleId> memberRoleIds = roles.stream()
                .map(MembershipRole::roleId)
                .collect(java.util.stream.Collectors.toSet());
        return memberRoleIds.containsAll(roleIds);
    }

    /**
     * Suspends this membership, preventing the user from accessing the tenant.
     */
    public void suspend() {
        this.status = MembershipStatus.SUSPENDED;
    }

    /**
     * Reactivates a suspended membership.
     */
    public void activate() {
        if (this.status == MembershipStatus.SUSPENDED) {
            this.status = MembershipStatus.ACTIVE;
        }
    }

    /**
     * Marks this membership as removed.
     */
    public void remove() {
        this.status = MembershipStatus.REMOVED;
    }

    /**
     * Checks if this membership is currently active.
     */
    public boolean isActive() {
        return status == MembershipStatus.ACTIVE;
    }

    /**
     * Checks if this membership is suspended.
     */
    public boolean isSuspended() {
        return status == MembershipStatus.SUSPENDED;
    }

    /**
     * Checks if this membership is removed.
     */
    public boolean isRemoved() {
        return status == MembershipStatus.REMOVED;
    }

    // Getters
    public TenantMembershipId getMembershipId() {
        return membershipId;
    }

    public UserId getUserId() {
        return userId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public UserId getInvitedBy() {
        return invitedBy;
    }

    public Set<MembershipRole> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
}

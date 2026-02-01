package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.TenantMembershipValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
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
 * <p>
 * Invariants:
 * - Active membership must have at least one role
 * - No duplicate roles by RoleId
 * - User can only have one membership per tenant
 * - Cannot revoke the last role of an active membership (must remove membership instead)
 */
public class TenantMembership {

    private final TenantMembershipId membershipId;
    private final UserId userId;
    private final TenantId tenantId;
    private final Instant joinedAt;
    private final UserId invitedBy;
    private final Set<MembershipRole> roles;
    private MembershipStatus status;

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
            val.addError(TenantMembershipValidationError.ID_EMPTY);
        }
        if (userId == null) {
            val.addError(TenantMembershipValidationError.USER_ID_EMPTY);
        }
        if (tenantId == null) {
            val.addError(TenantMembershipValidationError.TENANT_ID_EMPTY);
        }
        if (status == null) {
            val.addError(TenantMembershipValidationError.STATUS_EMPTY);
        }
        if (joinedAt == null) {
            val.addError(TenantMembershipValidationError.JOINED_AT_EMPTY);
        }
        if (invitedBy == null) {
            val.addError(TenantMembershipValidationError.INVITED_BY_EMPTY);
        }
        if (roles == null || roles.isEmpty()) {
            val.addError(TenantMembershipValidationError.ROLES_EMPTY);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        this.membershipId = membershipId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.status = status;
        this.joinedAt = joinedAt;
        this.invitedBy = invitedBy;
        assert roles != null;
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
     * Factory method to restore membership from the persistence layer.
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
     * @throws ErpValidationException if a role is already assigned
     */
    public void assignRole(RoleId roleId, RoleName roleName, UserId assignedBy) {
        var val = new ValidationError.Builder();
        if (roleId == null) {
            val.addError(TenantMembershipValidationError.ROLE_ID_EMPTY);
        }
        if (roleName == null) {
            val.addError(TenantMembershipValidationError.ROLE_NAME_EMPTY);
        }
        if (assignedBy == null) {
            val.addError(TenantMembershipValidationError.ASSIGNED_BY_EMPTY);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        boolean roleExists = roles.stream()
                .anyMatch(mr -> mr.roleId().equals(roleId));
        if (roleExists) {
            val.addError(TenantMembershipValidationError.ROLE_ALREADY_ASSIGNED);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        roles.add(MembershipRole.create(roleId, roleName, assignedBy));
    }

    /**
     * Revokes a role from this membership.
     * <p>
     * Invariant: Cannot revoke the last role of an active membership.
     *
     * @throws ErpValidationException if a role is not assigned or would violate invariant
     */
    public void revokeRole(RoleId roleId) {
        var val = new ValidationError.Builder();
        if (roleId == null) {
            throw new ErpAuthValidationException(
                    ValidationError.createSingle(TenantMembershipValidationError.ROLE_ID_EMPTY)
            );
        }

        MembershipRole roleToRevoke = roles.stream()
                .filter(mr -> mr.roleId().equals(roleId))
                .findFirst()
                .orElse(null);

        if (roleToRevoke == null) {
            val.addError(TenantMembershipValidationError.ROLE_NOT_ASSIGNED);
        }

        if (status == MembershipStatus.ACTIVE && roles.size() == 1) {
            val.addError(TenantMembershipValidationError.CANNOT_REVOKE_LAST_ROLE);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

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
     * Checks if this membership has all the provided roles.
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

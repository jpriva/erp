package com.jpriva.erpsp.auth.domain.services;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain service that coordinates operations between User, Tenant, Role, and TenantMembership.
 * <p>
 * Responsibilities:
 * - Validate that user, tenant, and roles exist
 * - Verify that the tenant is active
 * - Prevent duplicate memberships
 * - Keep TenantMembership.roles and Role.members synchronized
 * - Validate permissions (who can perform what action)
 */
public class TenantMembershipManager {
    private static final String USER_NOT_FOUND_ERROR = "User not found";
    private static final String TENANT_NOT_FOUND_ERROR = "Tenant not found";
    private static final String TENANT_NOT_ACTIVE_ERROR = "Tenant is not active";
    private static final String ROLE_NOT_FOUND_ERROR = "Role not found";
    private static final String ROLE_NOT_IN_TENANT_ERROR = "Role does not belong to the tenant";
    private static final String USER_ALREADY_MEMBER_ERROR = "User is already a member of this tenant";
    private static final String USER_NOT_MEMBER_ERROR = "User is not a member of this tenant";
    private static final String OWNER_CANNOT_BE_REMOVED_ERROR = "Owner cannot be removed from tenant. Transfer ownership first.";
    private static final String ROLE_NOT_ASSIGNED_ERROR = "Role is not assigned to this user in this tenant";

    private final TenantMembershipRepositoryPort membershipRepository;
    private final TenantRepositoryPort tenantRepository;
    private final RoleRepositoryPort roleRepository;

    public TenantMembershipManager(
            TenantMembershipRepositoryPort membershipRepository,
            TenantRepositoryPort tenantRepository,
            RoleRepositoryPort roleRepository
    ) {
        this.membershipRepository = membershipRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Adds a user as a member to a tenant with the specified roles.
     * Used when accepting invitations or inviting users manually.
     * </br>
     * Synchronizes both TenantMembership.roles and Role.members.
     *
     * @param userId   the user being added
     * @param tenantId the tenant the user is joining
     * @param roleIds  the roles to assign (must not be empty)
     * @param addedBy  the user performing this action (for audit)
     * @return the created membership
     * @throws ErpValidationException if validation fails
     */
    public TenantMembership addMemberToTenant(UserId userId, TenantId tenantId, Set<RoleId> roleIds, UserId addedBy) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            val.addError("roleIds", "At least one role must be assigned");
        }
        if (addedBy == null) {
            val.addError("addedBy", "Added by cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Verify tenant exists and is active
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    val.addError("tenantId", TENANT_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        if (!tenant.isActive()) {
            val.addError("tenantId", TENANT_NOT_ACTIVE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Prevent duplicate membership
        if (membershipRepository.existsByUserIdAndTenantId(userId, tenantId)) {
            val.addError("userId", USER_ALREADY_MEMBER_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Verify all roles exist and belong to the tenant
        Set<MembershipRole> membershipRoles = new HashSet<>();
        for (RoleId roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> {
                        var e = new ValidationError.Builder();
                        e.addError("roleId", ROLE_NOT_FOUND_ERROR);
                        return new ErpValidationException(AuthErrorCode.AUTH_MODULE, e.build());
                    });

            if (!role.getTenantId().equals(tenantId)) {
                val.addError("roleIds", ROLE_NOT_IN_TENANT_ERROR);
                ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
            }

            membershipRoles.add(MembershipRole.create(roleId, role.getName(), addedBy));
        }

        // Create membership
        TenantMembership membership = TenantMembership.create(userId, tenantId, membershipRoles, addedBy);
        membershipRepository.save(membership);

        // Synchronize Role.members: add user to all roles
        for (RoleId roleId : roleIds) {
            Role role = roleRepository.findById(roleId).orElseThrow();
            role.assignUser(userId);
            roleRepository.save(role);
        }

        return membership;
    }

    /**
     * Removes a user from a tenant.
     * Updates both TenantMembership (mark as REMOVED) and Role.members (remove user).
     * </br>
     * Invariant: Owner cannot be removed without transferring ownership first.
     *
     * @param userId    the user being removed
     * @param tenantId  the tenant from which the user is being removed
     * @param removedBy the user performing this action (for audit)
     * @throws ErpValidationException if validation fails
     */
    public void removeMemberFromTenant(UserId userId, TenantId tenantId, UserId removedBy) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (removedBy == null) {
            val.addError("removedBy", "Removed by cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Get tenant to check owner
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    val.addError("tenantId", TENANT_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Prevent removing the owner
        if (tenant.getOwnerId().equals(userId)) {
            val.addError("userId", OWNER_CANNOT_BE_REMOVED_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Get membership
        TenantMembership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Mark membership as removed
        membership.remove();
        membershipRepository.save(membership);

        // Synchronize Role.members: remove user from all roles
        for (MembershipRole membershipRole : membership.getRoles()) {
            Role role = roleRepository.findById(membershipRole.roleId()).orElseThrow();
            role.revokeUser(userId);
            roleRepository.save(role);
        }
    }

    /**
     * Assigns an additional role to an existing member.
     * </br>
     * Synchronizes both TenantMembership.roles and Role.members.
     *
     * @param userId     the user receiving the role
     * @param tenantId   the tenant context
     * @param roleId     the role to assign
     * @param assignedBy the user performing this action (for audit)
     * @throws ErpValidationException if validation fails
     */
    public void assignRoleToMember(UserId userId, TenantId tenantId, RoleId roleId, UserId assignedBy) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (roleId == null) {
            val.addError("roleId", "Role ID cannot be null");
        }
        if (assignedBy == null) {
            val.addError("assignedBy", "Assigned by cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Get membership
        TenantMembership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Get role and verify it belongs to the tenant
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    val.addError("roleId", ROLE_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        if (!role.getTenantId().equals(tenantId)) {
            val.addError("roleId", ROLE_NOT_IN_TENANT_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Assign role to membership
        membership.assignRole(roleId, role.getName(), assignedBy);
        membershipRepository.save(membership);

        // Synchronize Role.members
        role.assignUser(userId);
        roleRepository.save(role);
    }

    /**
     * Revokes a role from an existing member.
     * </br>
     * Invariant: Cannot revoke the last role of an active membership.
     * Use removeMemberFromTenant() to completely remove a user.
     * </br>
     * Synchronizes both TenantMembership.roles and Role.members.
     *
     * @param userId   the user losing the role
     * @param tenantId the tenant context
     * @param roleId   the role to revoke
     * @throws ErpValidationException if validation fails
     */
    public void revokeRoleFromMember(UserId userId, TenantId tenantId, RoleId roleId) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (roleId == null) {
            val.addError("roleId", "Role ID cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Get membership
        TenantMembership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Revoke role from membership (validates that it's assigned and not the last one)
        membership.revokeRole(roleId);
        membershipRepository.save(membership);

        // Synchronize Role.members
        Role role = roleRepository.findById(roleId).orElseThrow();
        role.revokeUser(userId);
        roleRepository.save(role);
    }

    /**
     * Suspends a member's access to the tenant.
     *
     * @param userId   the user to suspend
     * @param tenantId the tenant
     * @throws ErpValidationException if user is not a member
     */
    public void suspendMember(UserId userId, TenantId tenantId) {
        var val = new ValidationError.Builder();

        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        TenantMembership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        membership.suspend();
        membershipRepository.save(membership);
    }

    /**
     * Reactivates a suspended member.
     *
     * @param userId   the user to reactivate
     * @param tenantId the tenant
     * @throws ErpValidationException if user is not a member
     */
    public void activateMember(UserId userId, TenantId tenantId) {
        var val = new ValidationError.Builder();

        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        TenantMembership membership = membershipRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        membership.activate();
        membershipRepository.save(membership);
    }
}

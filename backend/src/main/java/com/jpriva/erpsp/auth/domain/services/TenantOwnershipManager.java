package com.jpriva.erpsp.auth.domain.services;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.List;

/**
 * Domain service that manages tenant ownership transfers.
 * </br>
 * Responsibilities:
 * - Transfer ownership to another active member with ADMIN role
 * - Validate permissions (only current owner can transfer)
 * - Validate that new owner meets requirements
 * - Update tenant ownership record
 * </br>
 * Note: After transfer, old owner can be removed from tenant.
 * New owner cannot be removed (enforced by TenantMembershipManager).
 */
public class TenantOwnershipManager {
    private static final String TENANT_NOT_FOUND_ERROR = "Tenant not found";
    private static final String CURRENT_OWNER_MISMATCH_ERROR = "Only the current owner can transfer ownership";
    private static final String NEW_OWNER_NOT_MEMBER_ERROR = "New owner is not a member of this tenant";
    private static final String NEW_OWNER_NOT_ACTIVE_ERROR = "New owner's membership is not active";
    private static final String NEW_OWNER_NOT_ADMIN_ERROR = "New owner must have the ADMIN role";
    private static final String ADMIN_ROLE_NOT_FOUND_ERROR = "ADMIN role not found for this tenant";

    private final TenantRepositoryPort tenantRepository;
    private final TenantMembershipRepositoryPort membershipRepository;
    private final RoleRepositoryPort roleRepository;

    public TenantOwnershipManager(
            TenantRepositoryPort tenantRepository,
            TenantMembershipRepositoryPort membershipRepository,
            RoleRepositoryPort roleRepository
    ) {
        this.tenantRepository = tenantRepository;
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Transfers ownership of a tenant to another member.
     * </br>
     * Validations:
     * - Current user is the actual owner
     * - New owner is an active member
     * - New owner has the ADMIN role
     * </br>
     * After transfer:
     * - tenant.ownerId is updated to newOwnerId
     * - New owner cannot be removed (enforced by TenantMembershipManager)
     * - Old owner can optionally be removed
     *
     * @param tenantId     the tenant whose ownership is being transferred
     * @param currentOwner the current owner (must match tenant's owner)
     * @param newOwner     the user who will become the new owner
     * @throws ErpValidationException if validation fails
     */
    public void transferOwnership(TenantId tenantId, UserId currentOwner, UserId newOwner) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (currentOwner == null) {
            val.addError("currentOwner", "Current owner cannot be null");
        }
        if (newOwner == null) {
            val.addError("newOwner", "New owner cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Prevent self-transfer
        if (currentOwner.equals(newOwner)) {
            val.addError("newOwner", "Cannot transfer ownership to the same user");
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Get tenant
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    val.addError("tenantId", TENANT_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Verify currentOwner is the actual owner
        if (!tenant.getOwnerId().equals(currentOwner)) {
            val.addError("currentOwner", CURRENT_OWNER_MISMATCH_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Verify new owner is an active member
        TenantMembership newOwnerMembership = membershipRepository.findByUserIdAndTenantId(newOwner, tenantId)
                .orElseThrow(() -> {
                    val.addError("newOwner", NEW_OWNER_NOT_MEMBER_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        if (!newOwnerMembership.isActive()) {
            val.addError("newOwner", NEW_OWNER_NOT_ACTIVE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Find ADMIN role for the tenant
        List<Role> tenantRoles = roleRepository.findByTenantId(tenantId);
        Role adminRole = tenantRoles.stream()
                .filter(r -> r.getName().value().equals("ADMIN"))
                .findFirst()
                .orElseThrow(() -> {
                    val.addError("roleId", ADMIN_ROLE_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Verify new owner has ADMIN role
        if (!newOwnerMembership.hasRole(adminRole.getRoleId())) {
            val.addError("newOwner", NEW_OWNER_NOT_ADMIN_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Transfer ownership
        tenant.transferOwnership(newOwner);
        tenantRepository.save(tenant);
    }
}

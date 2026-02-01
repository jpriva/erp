package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembershipId;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port for persisting and querying tenant memberships.
 * </br>
 * Implementations should ensure the following constraints:
 * - UNIQUE(user_id, tenant_id) to prevent duplicate memberships
 * - Cascade delete membership_roles when membership is deleted
 */
public interface TenantMembershipRepositoryPort {
    /**
     * Saves a tenant membership (insert or update).
     */
    void save(TenantMembership membership);

    /**
     * Finds a membership by its ID.
     */
    Optional<TenantMembership> findById(TenantMembershipId membershipId);

    /**
     * Finds a membership by user and tenant.
     */
    Optional<TenantMembership> findByUserIdAndTenantId(UserId userId, TenantId tenantId);

    /**
     * Finds all memberships for a user across all tenants.
     */
    List<TenantMembership> findByUserId(UserId userId);

    /**
     * Finds all active memberships for a user.
     */
    List<TenantMembership> findActiveByUserId(UserId userId);

    /**
     * Finds all memberships in a tenant (all statuses).
     */
    List<TenantMembership> findByTenantId(TenantId tenantId);

    /**
     * Finds all active memberships in a tenant.
     */
    List<TenantMembership> findActiveByTenantId(TenantId tenantId);

    /**
     * Finds all memberships that have a specific role in a tenant.
     */
    List<TenantMembership> findByTenantIdAndRoleId(TenantId tenantId, RoleId roleId);

    /**
     * Checks if a user is a member of a tenant.
     */
    boolean existsByUserIdAndTenantId(UserId userId, TenantId tenantId);

    /**
     * Deletes a membership by ID.
     */
    void deleteById(TenantMembershipId membershipId);
}

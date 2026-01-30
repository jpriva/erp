package com.jpriva.erpsp.auth.application.services;

import com.jpriva.erpsp.auth.application.dto.AssignRoleRequest;
import com.jpriva.erpsp.auth.application.dto.RemoveMemberRequest;
import com.jpriva.erpsp.auth.application.dto.TenantMembershipDto;
import com.jpriva.erpsp.auth.application.mappers.TenantMembershipMapper;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.services.TenantMembershipManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for tenant membership operations.
 * Orchestrates domain services and handles transactional boundaries.
 */
@Slf4j
@Service
public class TenantMembershipApplicationService {
    private final TenantMembershipManager membershipManager;
    private final TenantMembershipMapper mapper;

    public TenantMembershipApplicationService(
            TenantMembershipManager membershipManager,
            TenantMembershipMapper mapper
    ) {
        this.membershipManager = membershipManager;
        this.mapper = mapper;
    }

    /**
     * Gets a user's membership in a specific tenant.
     */
    @Transactional(readOnly = true)
    public TenantMembershipDto getMembership(UUID userId, UUID tenantId) {
        log.info("Fetching membership for user {} in tenant {}", userId, tenantId);

        return membershipManager
                .findByUserIdAndTenantId(new UserId(userId), new TenantId(tenantId))
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Membership not found"));
    }

    /**
     * Gets all memberships for a user across all tenants.
     */
    @Transactional(readOnly = true)
    public List<TenantMembershipDto> getUserMemberships(UUID userId) {
        log.info("Fetching all memberships for user {}", userId);

        return membershipManager
                .findByUserId(new UserId(userId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all active memberships for a user.
     */
    @Transactional(readOnly = true)
    public List<TenantMembershipDto> getActiveMemberships(UUID userId) {
        log.info("Fetching active memberships for user {}", userId);

        return membershipManager
                .findActiveByUserId(new UserId(userId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all members of a tenant.
     */
    @Transactional(readOnly = true)
    public List<TenantMembershipDto> getTenantMembers(UUID tenantId) {
        log.info("Fetching all members of tenant {}", tenantId);

        return membershipManager
                .findByTenantId(new TenantId(tenantId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all active members of a tenant.
     */
    @Transactional(readOnly = true)
    public List<TenantMembershipDto> getActiveTenantMembers(UUID tenantId) {
        log.info("Fetching active members of tenant {}", tenantId);

        return membershipManager
                .findActiveByTenantId(new TenantId(tenantId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Assigns a role to an existing member.
     */
    @Transactional
    public TenantMembershipDto assignRole(
            UUID userId,
            UUID tenantId,
            AssignRoleRequest request,
            UUID assignedBy
    ) {
        log.info("Assigning role {} to user {} in tenant {}", request.getRoleId(), userId, tenantId);

        membershipManager.assignRoleToMember(
                new UserId(userId),
                new TenantId(tenantId),
                new RoleId(request.getRoleId()),
                new UserId(assignedBy)
        );

        return membershipManager
                .findByUserIdAndTenantId(new UserId(userId), new TenantId(tenantId))
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Membership not found after role assignment"));
    }

    /**
     * Revokes a role from a member.
     */
    @Transactional
    public TenantMembershipDto revokeRole(UUID userId, UUID tenantId, UUID roleId) {
        log.info("Revoking role {} from user {} in tenant {}", roleId, userId, tenantId);

        membershipManager.revokeRoleFromMember(
                new UserId(userId),
                new TenantId(tenantId),
                new RoleId(roleId)
        );

        return membershipManager
                .findByUserIdAndTenantId(new UserId(userId), new TenantId(tenantId))
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Membership not found after role revocation"));
    }

    /**
     * Suspends a member's access to a tenant.
     */
    @Transactional
    public void suspendMember(UUID userId, UUID tenantId) {
        log.info("Suspending member {} from tenant {}", userId, tenantId);

        membershipManager.suspendMember(new UserId(userId), new TenantId(tenantId));
    }

    /**
     * Reactivates a suspended member.
     */
    @Transactional
    public void activateMember(UUID userId, UUID tenantId) {
        log.info("Activating member {} in tenant {}", userId, tenantId);

        membershipManager.activateMember(new UserId(userId), new TenantId(tenantId));
    }

    /**
     * Removes a member from a tenant.
     */
    @Transactional
    public void removeMember(UUID userId, UUID tenantId, RemoveMemberRequest request, UUID removedBy) {
        log.info("Removing member {} from tenant {}", userId, tenantId);

        membershipManager.removeMemberFromTenant(
                new UserId(userId),
                new TenantId(tenantId),
                new UserId(removedBy)
        );
    }

    // Package-private method used by InvitationApplicationService
    @Transactional
    TenantMembership addMemberToTenant(
            UserId userId,
            TenantId tenantId,
            Set<RoleId> roleIds,
            UserId addedBy
    ) {
        return membershipManager.addMemberToTenant(userId, tenantId, roleIds, addedBy);
    }

    // Package-private method used by TenantOwnershipApplicationService
    @Transactional
    void removeMemberFromTenant(UserId userId, TenantId tenantId, UserId removedBy) {
        membershipManager.removeMemberFromTenant(userId, tenantId, removedBy);
    }

    // Package-private for internal queries
    TenantMembership findByUserIdAndTenantId(UserId userId, TenantId tenantId) {
        return membershipManager.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));
    }

    List<TenantMembership> findByUserId(UserId userId) {
        return membershipManager.findByUserId(userId);
    }

    List<TenantMembership> findByTenantId(TenantId tenantId) {
        return membershipManager.findByTenantId(tenantId);
    }

    List<TenantMembership> findByTenantIdAndRoleId(TenantId tenantId, RoleId roleId) {
        return membershipManager.findByTenantIdAndRoleId(tenantId, roleId);
    }
}

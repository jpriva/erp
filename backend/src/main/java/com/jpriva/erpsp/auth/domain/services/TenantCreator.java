package com.jpriva.erpsp.auth.domain.services;

import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;

import java.util.HashSet;
import java.util.Set;

/**
 * Domain service that creates new tenants with their initial structure.
 * </br>
 * Responsibilities:
 * - Create a tenant and save it
 * - Create a default ADMIN role
 * - Create membership for the owner with an ADMIN role
 */
public class TenantCreator {

    private final TenantRepositoryPort tenantRepository;
    private final RoleRepositoryPort roleRepository;
    private final TenantMembershipRepositoryPort membershipRepository;

    public TenantCreator(
            TenantRepositoryPort tenantRepository,
            RoleRepositoryPort roleRepository,
            TenantMembershipRepositoryPort membershipRepository
    ) {
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * Creates a new tenant with owner membership and default ADMIN role.
     * </br>
     * Process:
     * 1. Create and save tenant
     * 2. Create and save the default ADMIN role
     * 3. Create and save owner membership with the ADMIN role
     *
     * @param ownerId    the user who will be the tenant owner
     * @param tenantName the name of the new tenant
     * @return the created tenant
     */
    public Tenant createTenant(UserId ownerId, String tenantName) {
        Tenant tenant = Tenant.create(ownerId, tenantName);
        tenantRepository.save(tenant);

        Role adminRole = Role.createDefaultRoles(tenant.getTenantId());
        adminRole.assignUser(ownerId);
        roleRepository.save(adminRole);

        Set<MembershipRole> ownerRoles = new HashSet<>();
        ownerRoles.add(MembershipRole.create(adminRole.getRoleId(), adminRole.getName(), ownerId));

        TenantMembership ownerMembership = TenantMembership.create(
                ownerId,
                tenant.getTenantId(),
                ownerRoles,
                ownerId
        );
        membershipRepository.save(ownerMembership);

        return tenant;
    }
}

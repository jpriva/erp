package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TenantMembership")
class TenantMembershipTest {
    private UserId userId;
    private UserId invitedBy;
    private TenantId tenantId;
    private RoleId roleId;
    private RoleName roleName;

    @BeforeEach
    void setUp() {
        userId = new UserId(UUID.randomUUID());
        invitedBy = new UserId(UUID.randomUUID());
        tenantId = new TenantId(UUID.randomUUID());
        roleId = new RoleId(UUID.randomUUID());
        roleName = new RoleName("ADMIN");
    }

    @Test
    @DisplayName("should create membership with initial roles")
    void testCreate() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));

        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        assertNotNull(membership.getMembershipId());
        assertEquals(userId, membership.getUserId());
        assertEquals(tenantId, membership.getTenantId());
        assertEquals(MembershipStatus.ACTIVE, membership.getStatus());
        assertEquals(1, membership.getRoles().size());
        assertTrue(membership.isActive());
    }

    @Test
    @DisplayName("should fail to create membership with empty roles")
    void testCreateWithEmptyRoles() {
        Set<MembershipRole> emptyRoles = new HashSet<>();

        assertThrows(ErpValidationException.class, () ->
                TenantMembership.create(userId, tenantId, emptyRoles, invitedBy)
        );
    }

    @Test
    @DisplayName("should assign role to membership")
    void testAssignRole() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        RoleId secondRoleId = new RoleId(UUID.randomUUID());
        RoleName secondRoleName = new RoleName("USER");

        membership.assignRole(secondRoleId, secondRoleName, invitedBy);

        assertEquals(2, membership.getRoles().size());
        assertTrue(membership.hasRole(secondRoleId));
    }

    @Test
    @DisplayName("should fail to assign duplicate role")
    void testAssignDuplicateRole() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        assertThrows(ErpValidationException.class, () ->
                membership.assignRole(roleId, roleName, invitedBy)
        );
    }

    @Test
    @DisplayName("should revoke role from membership")
    void testRevokeRole() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        roles.add(MembershipRole.create(new RoleId(UUID.randomUUID()), new RoleName("USER"), invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        membership.revokeRole(roleId);

        assertEquals(1, membership.getRoles().size());
        assertFalse(membership.hasRole(roleId));
    }

    @Test
    @DisplayName("should fail to revoke last role from active membership")
    void testRevokeLastRoleFromActiveMembership() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        assertThrows(ErpValidationException.class, () ->
                membership.revokeRole(roleId)
        );
    }

    @Test
    @DisplayName("should check if membership has role")
    void testHasRole() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        assertTrue(membership.hasRole(roleId));
        assertFalse(membership.hasRole(new RoleId(UUID.randomUUID())));
    }

    @Test
    @DisplayName("should suspend membership")
    void testSuspend() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        membership.suspend();

        assertTrue(membership.isSuspended());
        assertFalse(membership.isActive());
    }

    @Test
    @DisplayName("should activate suspended membership")
    void testActivate() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);
        membership.suspend();

        membership.activate();

        assertTrue(membership.isActive());
        assertFalse(membership.isSuspended());
    }

    @Test
    @DisplayName("should remove membership")
    void testRemove() {
        Set<MembershipRole> roles = new HashSet<>();
        roles.add(MembershipRole.create(roleId, roleName, invitedBy));
        TenantMembership membership = TenantMembership.create(userId, tenantId, roles, invitedBy);

        membership.remove();

        assertTrue(membership.isRemoved());
        assertFalse(membership.isActive());
    }
}

package com.jpriva.erpsp.auth.application.services;

import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import com.jpriva.erpsp.auth.domain.services.TenantMembershipManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test demonstrating tenant membership flows.
 * Tests the complete flow from domain services to application services.
 */
@DisplayName("Tenant Membership Flow")
@ExtendWith(MockitoExtension.class)
class TenantMembershipFlowTest {
    @Mock
    private TenantMembershipRepositoryPort membershipRepository;

    @Mock
    private TenantRepositoryPort tenantRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    private TenantMembershipManager membershipManager;

    private UserId userId;
    private UserId ownerUserId;
    private TenantId tenantId;
    private RoleId adminRoleId;
    private RoleId userRoleId;

    @BeforeEach
    void setUp() {
        membershipManager = new TenantMembershipManager(
                membershipRepository,
                tenantRepository,
                roleRepository
        );

        userId = new UserId(UUID.randomUUID());
        ownerUserId = new UserId(UUID.randomUUID());
        tenantId = new TenantId(UUID.randomUUID());
        adminRoleId = new RoleId(UUID.randomUUID());
        userRoleId = new RoleId(UUID.randomUUID());
    }

    @Test
    @DisplayName("should add member to tenant with single role")
    void testAddMemberWithSingleRole() {
        // Arrange
        Set<RoleId> roleIds = new HashSet<>();
        roleIds.add(adminRoleId);

        // Mock tenant existence
        when(tenantRepository.findById(tenantId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.tenant.Tenant(
                        tenantId,
                        ownerUserId,
                        new com.jpriva.erpsp.auth.domain.model.tenant.TenantName("Test Tenant"),
                        com.jpriva.erpsp.auth.domain.model.tenant.TenantStatus.ACTIVE,
                        java.time.Instant.now()
                ))
        );

        // Mock roles
        when(roleRepository.findById(adminRoleId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.role.Role(
                        adminRoleId,
                        tenantId,
                        new RoleName("ADMIN"),
                        new HashSet<>()
                ))
        );

        // Mock membership doesn't exist yet
        when(membershipRepository.existsByUserIdAndTenantId(userId, tenantId))
                .thenReturn(false);

        // Act
        TenantMembership membership = membershipManager.addMemberToTenant(
                userId, tenantId, roleIds, ownerUserId
        );

        // Assert
        assertNotNull(membership);
        assertEquals(userId, membership.getUserId());
        assertEquals(tenantId, membership.getTenantId());
        assertTrue(membership.isActive());
        assertEquals(1, membership.getRoles().size());
        assertTrue(membership.hasRole(adminRoleId));

        // Verify repository save was called
        verify(membershipRepository, times(1)).save(any(TenantMembership.class));
        verify(roleRepository, times(1)).save(any(com.jpriva.erpsp.auth.domain.model.role.Role.class));
    }

    @Test
    @DisplayName("should add member with multiple roles")
    void testAddMemberWithMultipleRoles() {
        // Arrange
        Set<RoleId> roleIds = new HashSet<>();
        roleIds.add(adminRoleId);
        roleIds.add(userRoleId);

        when(tenantRepository.findById(tenantId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.tenant.Tenant(
                        tenantId,
                        ownerUserId,
                        new com.jpriva.erpsp.auth.domain.model.tenant.TenantName("Test Tenant"),
                        com.jpriva.erpsp.auth.domain.model.tenant.TenantStatus.ACTIVE,
                        java.time.Instant.now()
                ))
        );

        when(roleRepository.findById(adminRoleId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.role.Role(
                        adminRoleId,
                        tenantId,
                        new RoleName("ADMIN"),
                        new HashSet<>()
                ))
        );

        when(roleRepository.findById(userRoleId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.role.Role(
                        userRoleId,
                        tenantId,
                        new RoleName("USER"),
                        new HashSet<>()
                ))
        );

        when(membershipRepository.existsByUserIdAndTenantId(userId, tenantId))
                .thenReturn(false);

        // Act
        TenantMembership membership = membershipManager.addMemberToTenant(
                userId, tenantId, roleIds, ownerUserId
        );

        // Assert
        assertEquals(2, membership.getRoles().size());
        assertTrue(membership.hasAllRoles(roleIds));

        verify(membershipRepository, times(1)).save(any(TenantMembership.class));
        verify(roleRepository, times(2)).save(any(com.jpriva.erpsp.auth.domain.model.role.Role.class));
    }

    @Test
    @DisplayName("should fail to add duplicate member")
    void testAddDuplicateMemberFails() {
        // Arrange
        Set<RoleId> roleIds = new HashSet<>();
        roleIds.add(adminRoleId);

        when(tenantRepository.findById(tenantId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.tenant.Tenant(
                        tenantId,
                        ownerUserId,
                        new com.jpriva.erpsp.auth.domain.model.tenant.TenantName("Test Tenant"),
                        com.jpriva.erpsp.auth.domain.model.tenant.TenantStatus.ACTIVE,
                        java.time.Instant.now()
                ))
        );

        // Member already exists
        when(membershipRepository.existsByUserIdAndTenantId(userId, tenantId))
                .thenReturn(true);

        // Act & Assert
        assertThrows(Exception.class, () ->
                membershipManager.addMemberToTenant(userId, tenantId, roleIds, ownerUserId)
        );

        // Membership should not be saved
        verify(membershipRepository, never()).save(any(TenantMembership.class));
    }

    @Test
    @DisplayName("should assign additional role to existing member")
    void testAssignAdditionalRole() {
        // Arrange
        Set<RoleId> initialRoles = new HashSet<>();
        initialRoles.add(adminRoleId);

        TenantMembership membership = TenantMembership.create(
                userId, tenantId, new HashSet<>() {{
                    add(com.jpriva.erpsp.auth.domain.model.membership.MembershipRole.create(
                            adminRoleId, new RoleName("ADMIN"), ownerUserId
                    ));
                }}, ownerUserId
        );

        when(membershipRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(membership));

        when(roleRepository.findById(userRoleId)).thenReturn(
                Optional.of(new com.jpriva.erpsp.auth.domain.model.role.Role(
                        userRoleId,
                        tenantId,
                        new RoleName("USER"),
                        new HashSet<>()
                ))
        );

        // Act
        membershipManager.assignRoleToMember(userId, tenantId, userRoleId, ownerUserId);

        // Assert
        verify(membershipRepository, times(1)).save(any(TenantMembership.class));
        verify(roleRepository, times(1)).save(any(com.jpriva.erpsp.auth.domain.model.role.Role.class));
    }

    @Test
    @DisplayName("should suspend member")
    void testSuspendMember() {
        // Arrange
        Set<RoleId> roleIds = new HashSet<>();
        roleIds.add(adminRoleId);

        TenantMembership membership = TenantMembership.create(
                userId, tenantId, new HashSet<>() {{
                    add(com.jpriva.erpsp.auth.domain.model.membership.MembershipRole.create(
                            adminRoleId, new RoleName("ADMIN"), ownerUserId
                    ));
                }}, ownerUserId
        );

        when(membershipRepository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(Optional.of(membership));

        // Act
        membershipManager.suspendMember(userId, tenantId);

        // Assert
        verify(membershipRepository, times(1)).save(any(TenantMembership.class));
    }
}

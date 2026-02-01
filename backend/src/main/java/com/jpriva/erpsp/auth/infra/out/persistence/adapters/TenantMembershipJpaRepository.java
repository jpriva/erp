package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.TenantMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for TenantMembershipEntity.
 * Provides database access for tenant memberships.
 */
@Repository
public interface TenantMembershipJpaRepository extends JpaRepository<TenantMembershipEntity, UUID> {
    /**
     * Finds a membership by user and tenant.
     */
    Optional<TenantMembershipEntity> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    /**
     * Finds all memberships for a user.
     */
    List<TenantMembershipEntity> findByUserId(UUID userId);

    /**
     * Finds all active memberships for a user.
     */
    List<TenantMembershipEntity> findByUserIdAndStatus(UUID userId, String status);

    /**
     * Finds all memberships in a tenant.
     */
    List<TenantMembershipEntity> findByTenantId(UUID tenantId);

    /**
     * Finds all active memberships in a tenant.
     */
    List<TenantMembershipEntity> findByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Finds all memberships that have a specific role in a tenant.
     */
    @Query("""
            SELECT tm FROM TenantMembershipEntity tm
            JOIN tm.roles mr
            WHERE tm.tenantId = :tenantId AND mr.roleId = :roleId
            """)
    List<TenantMembershipEntity> findByTenantIdAndRoleId(
            @Param("tenantId") UUID tenantId,
            @Param("roleId") UUID roleId
    );

    /**
     * Checks if a user is a member of a tenant.
     */
    boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);
}

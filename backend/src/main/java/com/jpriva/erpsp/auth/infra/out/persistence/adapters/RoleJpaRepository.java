package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data JPA repository for RoleEntity.
 */
@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, UUID> {
    /**
     * Finds all roles for a tenant.
     */
    List<RoleEntity> findByTenantId(UUID tenantId);

    /**
     * Finds a role by tenant and name.
     */
    Optional<RoleEntity> findByTenantIdAndName(UUID tenantId, String name);

    /**
     * Finds multiple roles by their IDs (batch fetch).
     */
    List<RoleEntity> findByRoleIdIn(Set<UUID> roleIds);
}

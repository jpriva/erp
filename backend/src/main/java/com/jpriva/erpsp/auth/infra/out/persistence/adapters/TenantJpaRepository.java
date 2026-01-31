package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for TenantEntity.
 */
@Repository
public interface TenantJpaRepository extends JpaRepository<TenantEntity, UUID> {
    /**
     * Finds all tenants owned by a specific user.
     */
    List<TenantEntity> findByOwnerId(UUID ownerId);
}

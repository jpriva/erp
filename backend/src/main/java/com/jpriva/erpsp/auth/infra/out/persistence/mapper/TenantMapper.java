package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.TenantEntity;

/**
 * Mapper for converting between Tenant domain model and TenantEntity JPA entity.
 */
public class TenantMapper {
    private TenantMapper() {
    }

    /**
     * Converts a Tenant domain model to a TenantEntity JPA entity.
     *
     * @param tenant the Tenant domain model
     * @return the TenantEntity JPA entity
     */
    public static TenantEntity domainToEntity(Tenant tenant) {
        return TenantEntity.builder()
                .tenantId(tenant.getTenantId().value())
                .ownerId(tenant.getOwnerId().value())
                .name(tenant.getName().value())
                .status(tenant.getStatus().toString())
                .build();
    }

    /**
     * Converts a TenantEntity JPA entity to a Tenant domain model.
     *
     * @param entity the TenantEntity JPA entity
     * @return the Tenant domain model
     */
    public static Tenant entityToDomain(TenantEntity entity) {
        return Tenant.fromPersistence(
                entity.getTenantId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}

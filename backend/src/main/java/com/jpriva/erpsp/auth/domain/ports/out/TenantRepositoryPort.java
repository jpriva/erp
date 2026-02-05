package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port for tenant persistence operations.
 */
public interface TenantRepositoryPort {
    /**
     * Saves a tenant (insert or update).
     */
    void save(Tenant tenant);

    /**
     * Finds a tenant by its ID.
     */
    Optional<Tenant> findById(TenantId tenantId);

    /**
     * Finds all tenants owned by a specific user.
     */
    List<Tenant> findByOwnerId(UserId ownerId);
}

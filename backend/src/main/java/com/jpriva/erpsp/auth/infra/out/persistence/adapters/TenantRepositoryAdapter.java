package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.TenantEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.TenantMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements TenantRepositoryPort using Spring Data JPA.
 * Converts between domain model (Tenant) and JPA entity (TenantEntity).
 */
@Component
public class TenantRepositoryAdapter implements TenantRepositoryPort {
    private final TenantJpaRepository jpaRepository;

    public TenantRepositoryAdapter(TenantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Tenant tenant) {
        TenantEntity entity = TenantMapper.domainToEntity(tenant);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Tenant> findById(TenantId tenantId) {
        return jpaRepository.findById(tenantId.value())
                .map(TenantMapper::entityToDomain);
    }

    @Override
    public List<Tenant> findByOwnerId(UserId ownerId) {
        return jpaRepository.findByOwnerId(ownerId.value())
                .stream()
                .map(TenantMapper::entityToDomain)
                .collect(Collectors.toList());
    }
}

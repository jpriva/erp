package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembershipId;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.TenantMembershipMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements TenantMembershipRepositoryPort using Spring Data JPA.
 * Converts between domain model (TenantMembership) and JPA entity (TenantMembershipEntity).
 */
@Component
public class TenantMembershipRepositoryAdapter implements TenantMembershipRepositoryPort {
    private final TenantMembershipJpaRepository jpaRepository;

    public TenantMembershipRepositoryAdapter(TenantMembershipJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(TenantMembership membership) {
        var entity = TenantMembershipMapper.domainToEntity(membership);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<TenantMembership> findById(TenantMembershipId membershipId) {
        return jpaRepository.findById(membershipId.value())
                .map(TenantMembershipMapper::entityToDomain);
    }

    @Override
    public Optional<TenantMembership> findByUserIdAndTenantId(UserId userId, TenantId tenantId) {
        return jpaRepository.findByUserIdAndTenantId(userId.value(), tenantId.value())
                .map(TenantMembershipMapper::entityToDomain);
    }

    @Override
    public List<TenantMembership> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(TenantMembershipMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findActiveByUserId(UserId userId) {
        return jpaRepository.findByUserIdAndStatus(userId.value(), "ACTIVE")
                .stream()
                .map(TenantMembershipMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantId(tenantId.value())
                .stream()
                .map(TenantMembershipMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findActiveByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantIdAndStatus(tenantId.value(), "ACTIVE")
                .stream()
                .map(TenantMembershipMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findByTenantIdAndRoleId(TenantId tenantId, RoleId roleId) {
        return jpaRepository.findByTenantIdAndRoleId(tenantId.value(), roleId.value())
                .stream()
                .map(TenantMembershipMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserIdAndTenantId(UserId userId, TenantId tenantId) {
        return jpaRepository.existsByUserIdAndTenantId(userId.value(), tenantId.value());
    }

    @Override
    public void deleteById(TenantMembershipId membershipId) {
        jpaRepository.deleteById(membershipId.value());
    }
}

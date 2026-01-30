package com.jpriva.erpsp.auth.infra.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembershipId;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.TenantMembershipRepositoryPort;
import com.jpriva.erpsp.auth.infra.persistence.entities.MembershipRoleEntity;
import com.jpriva.erpsp.auth.infra.persistence.entities.TenantMembershipEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        TenantMembershipEntity entity = domainToEntity(membership);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<TenantMembership> findById(TenantMembershipId membershipId) {
        return jpaRepository.findById(membershipId.value())
                .map(this::entityToDomain);
    }

    @Override
    public Optional<TenantMembership> findByUserIdAndTenantId(UserId userId, TenantId tenantId) {
        return jpaRepository.findByUserIdAndTenantId(userId.value(), tenantId.value())
                .map(this::entityToDomain);
    }

    @Override
    public List<TenantMembership> findByUserId(UserId userId) {
        return jpaRepository.findByUserId(userId.value())
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findActiveByUserId(UserId userId) {
        return jpaRepository.findByUserIdAndStatus(userId.value(), "ACTIVE")
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantId(tenantId.value())
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findActiveByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantIdAndStatus(tenantId.value(), "ACTIVE")
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantMembership> findByTenantIdAndRoleId(TenantId tenantId, RoleId roleId) {
        return jpaRepository.findByTenantIdAndRoleId(tenantId.value(), roleId.value())
                .stream()
                .map(this::entityToDomain)
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

    /**
     * Converts domain model to JPA entity.
     */
    private TenantMembershipEntity domainToEntity(TenantMembership domain) {
        Set<MembershipRoleEntity> roleEntities = domain.getRoles()
                .stream()
                .map(this::membershipRoleToDomainEntity)
                .collect(Collectors.toSet());

        return TenantMembershipEntity.builder()
                .membershipId(domain.getMembershipId().value())
                .userId(domain.getUserId().value())
                .tenantId(domain.getTenantId().value())
                .status(domain.getStatus().toString())
                .joinedAt(domain.getJoinedAt())
                .invitedBy(domain.getInvitedBy().value())
                .roles(roleEntities)
                .build();
    }

    /**
     * Converts JPA entity to domain model.
     */
    private TenantMembership entityToDomain(TenantMembershipEntity entity) {
        Set<MembershipRole> roles = entity.getRoles()
                .stream()
                .map(this::membershipRoleEntityToDomain)
                .collect(Collectors.toSet());

        return TenantMembership.fromPersistence(
                entity.getMembershipId(),
                entity.getUserId(),
                entity.getTenantId(),
                entity.getStatus(),
                entity.getJoinedAt(),
                entity.getInvitedBy(),
                roles
        );
    }

    /**
     * Converts domain MembershipRole to JPA entity.
     */
    private MembershipRoleEntity membershipRoleToDomainEntity(MembershipRole domain) {
        return MembershipRoleEntity.builder()
                .membershipId(null) // Set by JPA relationship
                .roleId(domain.roleId().value())
                .roleName(domain.roleName().value())
                .assignedAt(domain.assignedAt())
                .assignedBy(domain.assignedBy().value())
                .build();
    }

    /**
     * Converts JPA MembershipRoleEntity to domain model.
     */
    private MembershipRole membershipRoleEntityToDomain(MembershipRoleEntity entity) {
        return MembershipRole.fromPersistence(
                entity.getRoleId(),
                entity.getRoleName(),
                entity.getAssignedAt(),
                entity.getAssignedBy()
        );
    }
}

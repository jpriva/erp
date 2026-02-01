package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.RoleEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.RoleMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter that implements RoleRepositoryPort using Spring Data JPA.
 * Converts between domain model (Role) and JPA entity (RoleEntity).
 * <p>
 * Special handling for Role.members: Members are stored in the membership_roles table,
 * not directly in the roles table. When loading a role, we query membership_roles
 * to populate the members set. When saving a role, we do NOT update members
 * (they are managed by TenantMembershipManager).
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {
    private final RoleJpaRepository jpaRepository;
    private final TenantMembershipJpaRepository membershipRepository;

    public RoleRepositoryAdapter(RoleJpaRepository jpaRepository, TenantMembershipJpaRepository membershipRepository) {
        this.jpaRepository = jpaRepository;
        this.membershipRepository = membershipRepository;
    }

    @Override
    public void save(Role role) {
        RoleEntity entity = RoleMapper.domainToEntity(role);
        jpaRepository.save(entity);
        // NOTE: Members are not updated here. They are managed by TenantMembershipManager.
    }

    @Override
    public Optional<Role> findById(RoleId roleId) {
        return jpaRepository.findById(roleId.value())
                .map(entity -> {
                    Set<UserId> members = loadMembersForRole(roleId);
                    return RoleMapper.entityToDomain(entity, members);
                });
    }

    @Override
    public List<Role> findByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantId(tenantId.value())
                .stream()
                .map(entity -> {
                    Set<UserId> members = loadMembersForRole(new RoleId(entity.getRoleId()));
                    return RoleMapper.entityToDomain(entity, members);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findByTenantIdAndName(TenantId tenantId, RoleName roleName) {
        return jpaRepository.findByTenantIdAndName(tenantId.value(), roleName.value())
                .map(entity -> {
                    Set<UserId> members = loadMembersForRole(new RoleId(entity.getRoleId()));
                    return RoleMapper.entityToDomain(entity, members);
                });
    }

    @Override
    public List<Role> findByIds(Set<RoleId> roleIds) {
        Set<java.util.UUID> uuids = roleIds.stream()
                .map(RoleId::value)
                .collect(Collectors.toSet());

        return jpaRepository.findByRoleIdIn(uuids)
                .stream()
                .map(entity -> {
                    Set<UserId> members = loadMembersForRole(new RoleId(entity.getRoleId()));
                    return RoleMapper.entityToDomain(entity, members);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(RoleId roleId) {
        jpaRepository.deleteById(roleId.value());
    }

    /**
     * Loads all user IDs that are members of a role.
     * Members are stored in the membership_roles table through tenant_memberships.
     * <p>
     * Query logic:
     * - Find all membership_roles entries for this role
     * - Join with tenant_memberships to get the user_id
     * - Only include ACTIVE memberships
     */
    private Set<UserId> loadMembersForRole(RoleId roleId) {
        // This would require a custom query on the JPA repository.
        // For now, return empty set. The actual implementation depends on
        // having access to membership_roles through a custom query.
        // TODO: Implement custom query to load members from membership_roles
        return Set.of();
    }
}

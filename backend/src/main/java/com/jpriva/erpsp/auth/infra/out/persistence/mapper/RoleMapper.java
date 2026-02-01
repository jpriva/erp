package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.RoleEntity;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Role domain model and RoleEntity JPA entity.
 * </br>
 * NOTE: Role.members are NOT persisted directly in RoleEntity.
 * They are stored in the membership_roles table and must be loaded separately.
 */
public class RoleMapper {
    private RoleMapper() {
    }

    /**
     * Converts a Role domain model to a RoleEntity JPA entity.
     * </br>
     * NOTE: Members are not included in the conversion as they are managed
     * separately through the membership_roles table.
     */
    public static RoleEntity domainToEntity(Role role) {
        return RoleEntity.builder()
                .roleId(role.getRoleId().value())
                .tenantId(role.getTenantId().value())
                .name(role.getName().value())
                .build();
    }

    /**
     * Converts a RoleEntity JPA entity to a Role domain model.
     *
     * @param entity  the JPA entity
     * @param members the set of user IDs that are members of this role
     *                (loaded from membership_roles table)
     * @return the domain model with members populated
     */
    public static Role entityToDomain(RoleEntity entity, Set<UserId> members) {
        Set<UUID> memberUuids = members.stream()
                .map(UserId::value)
                .collect(Collectors.toSet());

        return Role.fromPersistence(
                entity.getRoleId(),
                entity.getTenantId(),
                entity.getName(),
                memberUuids
        );
    }

    /**
     * Converts a RoleEntity JPA entity to a Role domain model with empty members set.
     * Use this when members will be loaded separately.
     */
    public static Role entityToDomainWithoutMembers(RoleEntity entity) {
        return Role.fromPersistence(
                entity.getRoleId(),
                entity.getTenantId(),
                entity.getName(),
                Set.of()
        );
    }
}

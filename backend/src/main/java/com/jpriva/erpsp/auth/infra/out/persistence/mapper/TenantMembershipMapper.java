package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.MembershipRoleEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.TenantMembershipEntity;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between TenantMembership domain model and TenantMembershipEntity JPA entity.
 */
public class TenantMembershipMapper {
    private TenantMembershipMapper() {
    }

    /**
     * Converts a TenantMembership domain model to a TenantMembershipEntity JPA entity.
     */
    public static TenantMembershipEntity domainToEntity(TenantMembership domain) {
        Set<MembershipRoleEntity> roleEntities = domain.getRoles()
                .stream()
                .map(TenantMembershipMapper::membershipRoleToDomainEntity)
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
     * Converts a TenantMembershipEntity JPA entity to a TenantMembership domain model.
     */
    public static TenantMembership entityToDomain(TenantMembershipEntity entity) {
        Set<MembershipRole> roles = entity.getRoles()
                .stream()
                .map(TenantMembershipMapper::membershipRoleEntityToDomain)
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
     * Converts a domain MembershipRole to a JPA entity.
     */
    private static MembershipRoleEntity membershipRoleToDomainEntity(MembershipRole domain) {
        return MembershipRoleEntity.builder()
                .membershipId(null) // Set by JPA relationship
                .roleId(domain.roleId().value())
                .roleName(domain.roleName().value())
                .assignedAt(domain.assignedAt())
                .assignedBy(domain.assignedBy().value())
                .build();
    }

    /**
     * Converts a JPA MembershipRoleEntity to a domain model.
     */
    private static MembershipRole membershipRoleEntityToDomain(MembershipRoleEntity entity) {
        return MembershipRole.fromPersistence(
                entity.getRoleId(),
                entity.getRoleName(),
                entity.getAssignedAt(),
                entity.getAssignedBy()
        );
    }
}
